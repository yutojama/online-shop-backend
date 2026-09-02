package com.portshop.product_service.repository;

import com.portshop.product_service.model.ItemReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemReservationRepository extends JpaRepository<ItemReservation, UUID> {
    List<ItemReservation> getItemReservationByOrderId(String orderId);
}
