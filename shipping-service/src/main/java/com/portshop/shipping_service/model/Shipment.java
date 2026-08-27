package com.portshop.shipping_service.model;

import com.portshop.shipping_service.shippingEnum.Carrier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
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
    private Carrier carrier;

    @Column(nullable = false)
    private String trackingNumber;

    private Instant shippedAt;

    private Instant deliveredAt;

}
