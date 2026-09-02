package com.portshop.product_service.service;

import com.portshop.product_service.exception.ItemReservationException;
import com.portshop.product_service.model.ItemReservation;
import com.portshop.product_service.repository.ItemReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final ItemReservationRepository itemReservationRepository;

    public InventoryService(ItemReservationRepository itemReservationRepository) {
        this.itemReservationRepository = itemReservationRepository;
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

}
