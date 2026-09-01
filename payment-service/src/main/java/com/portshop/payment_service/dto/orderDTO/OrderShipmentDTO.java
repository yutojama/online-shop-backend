package com.portshop.payment_service.dto.orderDTO;

import com.portshop.payment_service.paymentEnum.Carrier;
import com.portshop.payment_service.paymentEnum.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderShipmentDTO {

    private String shipmentId;
    private String shopKeycloakId;
    private Carrier carrier;
    private String trackingNumber;
    private ShipmentStatus shipmentStatus;
    @Builder.Default
    private List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

    public void addOrderItemDTO(OrderItemDTO orderItemDTO){
        orderItemDTOList.add(orderItemDTO);
    }

}
