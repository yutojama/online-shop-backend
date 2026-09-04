package com.portshop.order_service.repository;

import com.portshop.order_service.model.ItemChangeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItemChangeRecordRepository extends JpaRepository<ItemChangeRecord, UUID> {

    ItemChangeRecord getItemChangeRecordByItemId(String itemId);

    ItemChangeRecord getItemChangeRecordByItemIdAndOrderId(String itemId, String orderId);
}
