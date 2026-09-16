package com.portshop.order_service.repository;

import com.portshop.order_service.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    OrderItem getOrderItemById(UUID Id);

    OrderItem getOrderItemByItemId(String itemId);
}
