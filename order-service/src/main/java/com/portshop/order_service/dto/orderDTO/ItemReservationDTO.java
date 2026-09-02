package com.portshop.order_service.dto.orderDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemReservationDTO {

    private String orderId;
    private String itemId;
    private Integer quantity;
    private Integer availability;
    private String message;

}
