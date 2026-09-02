package com.portshop.product_service.service;

import com.portshop.product_service.dto.orderDTO.ItemReservationDTO;
import com.portshop.product_service.dto.orderDTO.OrderDTO;
import com.portshop.product_service.exception.ItemReservationException;
import com.portshop.product_service.model.Item;
import com.portshop.product_service.model.ItemReservation;
import com.portshop.product_service.productEnum.InventoryStatus;
import com.portshop.product_service.repository.ItemReservationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class InventoryService {

    private final ItemReservationRepository itemReservationRepository;
    private final ItemService itemService;

    public InventoryService(ItemReservationRepository itemReservationRepository, ItemService itemService) {
        this.itemReservationRepository = itemReservationRepository;
        this.itemService = itemService;
    }


    public ItemReservation save(ItemReservation itemReservation) {
        return itemReservationRepository.save(itemReservation);
    }

    public List<ItemReservation> getItemReservationByOrderId(String orderId) {
        List<ItemReservation> items = itemReservationRepository.getItemReservationByOrderId(orderId);
        if(items.isEmpty()){
            throw new ItemReservationException("ItemReservation not found, orderId: "+orderId);
        }
        return items;
    }

    public List<ItemReservationDTO> reserveItem(OrderDTO orderDTO) {
        List<ItemReservationDTO> itemReservationDTOs = new ArrayList<>();
        List<ItemReservation> itemReservations = new ArrayList<>();

        orderDTO.getOrderShipmentDTOList().forEach(orderShipmentDTO -> {
            orderShipmentDTO.getOrderItemDTOList().forEach(orderItemDTO -> {
                Item item = itemService.getItem(UUID.fromString(orderItemDTO.getItemId()));
                String reserveMessage = orderItemDTO.getQuantity()>item.getQuantity() ?"REJECTED":"RESERVED";
                itemReservationDTOs.add(ItemReservationDTO.builder()
                                .orderId(orderDTO.getOrderId())
                                .itemId(orderItemDTO.getItemId())
                                .quantity(orderItemDTO.getQuantity())
                                .availability(item.getQuantity())
                                .message(reserveMessage)
                        .build()
                );
                itemReservations.add(
                        ItemReservation.builder()
                                .orderId(orderDTO.getOrderId())
                                .itemId(orderItemDTO.getItemId())
                                .quantity(orderItemDTO.getQuantity())
                                .status(InventoryStatus.RESERVED)
                                .build()
                );
            });
        });

        if(itemReservationDTOs.stream()
                .noneMatch(itemReservationDTO ->
                        itemReservationDTO.getMessage().equals("REJECTED")
                )
        ) {
            itemReservationRepository.saveAll(itemReservations);

            // take from stock
            itemReservations.forEach(item -> {
                Item getItem = itemService.getItem(UUID.fromString(item.getItemId()));
                getItem.setQuantity(getItem.getQuantity() - item.getQuantity());
                itemService.saveItem(getItem);
            });
        }
        return itemReservationDTOs;
    }

    public void cancelItemReservation(String orderId) {
        List<ItemReservation> itemReservations = itemReservationRepository.getItemReservationByOrderId(orderId);
        if(itemReservations.isEmpty()){
            throw new ItemReservationException("ItemReservation not found, orderId: "+orderId);
        }
        itemReservations.forEach(itemReservation -> {
            itemReservation.setStatus(InventoryStatus.CANCELLED);
            // restore stock
            Item getItem = itemService.getItem(UUID.fromString(itemReservation.getItemId()));
            getItem.setQuantity(getItem.getQuantity() + itemReservation.getQuantity());
            itemService.saveItem(getItem);
        });
        itemReservationRepository.saveAll(itemReservations);
    }

}
