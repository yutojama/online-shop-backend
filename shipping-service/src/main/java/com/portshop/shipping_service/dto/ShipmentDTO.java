package com.portshop.shipping_service.dto;

import com.portshop.shipping_service.shippingEnum.Carrier;
import com.portshop.shipping_service.shippingEnum.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentDTO {

    private String shipmentId;
    private String orderId;
    private Carrier carrier;
    private ShipmentStatus shipmentStatus;
    private String trackingNumber;
    private Instant shippedAt;
    private Instant deliveredAt;

}
