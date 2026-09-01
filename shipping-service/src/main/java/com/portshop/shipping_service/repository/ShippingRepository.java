package com.portshop.shipping_service.repository;

import com.portshop.shipping_service.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShippingRepository extends JpaRepository<Shipment, UUID> {

    boolean existsShipmentByOrderId(String orderId);

    List<Shipment> getShipmentByOrderId(String orderId);
}
