package com.portshop.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * this dto is contained in CartSumResponseDTO to list product name and quantity
 * */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemShortResponseDTO {

    @NotBlank(message = "Product name is required for sum")
    private String productName;

    @NotNull(message = "Quantity is required for sum")
    private Integer quantity;

}
