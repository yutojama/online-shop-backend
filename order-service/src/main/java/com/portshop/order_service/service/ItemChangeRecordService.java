package com.portshop.order_service.service;

import com.portshop.order_service.model.*;
import com.portshop.order_service.orderEnum.OrderStatus;
import com.portshop.order_service.repository.ItemChangeRecordRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ItemChangeRecordService {

    private final ItemChangeRecordRepository itemChangeRecordRepository;

    public ItemChangeRecordService(ItemChangeRecordRepository itemChangeRecordRepository) {
        this.itemChangeRecordRepository = itemChangeRecordRepository;
    }

    public void createItemHistoryByOrder(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        orderItems.forEach(orderItem -> {
            // create item change record for each order item
            ItemChangeRecord itemChangeRecord = ItemChangeRecord.builder()
                    .shopKeycloakId(orderItem.getShopKeycloakId())
                    .itemId(orderItem.getItemId())
                    .orderId(order.getId().toString())
                    .build();
            itemChangeRecord.addItemHistory(
                    ItemHistory.builder()
                            .toStatus(OrderStatus.PENDING)
                            .changedAt(Instant.now())
                            .changedBy("Customer")
                            .reason("Order created")
                            .itemChangeRecord(itemChangeRecord)
                            .build()
            );
            itemChangeRecordRepository.save(itemChangeRecord);
        });
    }

    public void addItemHistoryByOrder(
            Order order,
            OrderStatus fromStatus,
            OrderStatus toStatus,
            String changedBy,
            String reason
    ) {
        List<OrderItem> orderItems = order.getOrderItems();
        orderItems.forEach(orderItem -> {
            // create item change record for each order item
            ItemChangeRecord itemChangeRecord = itemChangeRecordRepository.getItemChangeRecordByItemIdAndOrderId(orderItem.getItemId(),order.getId().toString());
            itemChangeRecord.addItemHistory(
                    ItemHistory.builder()
                            .fromStatus(fromStatus)
                            .toStatus(toStatus)
                            .changedAt(Instant.now())
                            .changedBy(changedBy)
                            .reason(reason)
                            .itemChangeRecord(itemChangeRecord)
                            .build()
            );
            itemChangeRecordRepository.save(itemChangeRecord);
        });
    }

    public void addItemHistoryByShipment(
            Shipment shipment,
            OrderStatus fromStatus,
            OrderStatus toStatus,
            String changedBy,
            String reason
    ) {
        shipment.getOrderItems().forEach(orderItem -> {
            // create item change record for each order item
            ItemChangeRecord itemChangeRecord = itemChangeRecordRepository.getItemChangeRecordByItemId(orderItem.getItemId());
            itemChangeRecord.addItemHistory(
                    ItemHistory.builder()
                            .fromStatus(fromStatus)
                            .toStatus(toStatus)
                            .changedAt(Instant.now())
                            .changedBy(changedBy)
                            .reason(reason)
                            .itemChangeRecord(itemChangeRecord)
                            .build()
            );
            itemChangeRecordRepository.save(itemChangeRecord);
        });
    }

}
