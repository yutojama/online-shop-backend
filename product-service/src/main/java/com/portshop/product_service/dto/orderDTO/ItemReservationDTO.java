package com.portshop.product_service.dto.orderDTO;

import com.portshop.product_service.productEnum.InventoryStatus;
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
