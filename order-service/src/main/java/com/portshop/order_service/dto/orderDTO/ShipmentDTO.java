package com.portshop.order_service.dto.orderDTO;

import com.portshop.order_service.orderEnum.Carrier;
import com.portshop.order_service.orderEnum.ShipmentStatus;
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
