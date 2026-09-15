package com.portshop.shipping_service.model;

import com.portshop.shipping_service.shippingEnum.Carrier;
import com.portshop.shipping_service.shippingEnum.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String shopId;

    @Column(nullable = false)
    private String orderShipmentId;

    @Column(nullable = false)
    private Carrier carrier;

    @Column(nullable = false)
    private ShipmentStatus shipmentStatus;

    @Column(nullable = false)
    private String trackingNumber;

    private Instant shippedAt;

    private Instant deliveredAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
