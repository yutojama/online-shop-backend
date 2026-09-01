package com.portshop.order_service.dto.orderDTO;

import com.portshop.order_service.dto.AddressRequestDTO;
import com.portshop.order_service.model.Shipment;
import com.portshop.order_service.orderEnum.Carrier;
import com.portshop.order_service.orderEnum.ShipmentStatus;
import jakarta.validation.constraints.NotBlank;
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
