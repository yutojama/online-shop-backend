package com.portshop.order_service.dto;

import com.portshop.order_service.orderEnum.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


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

    @NotNull(message = "Price is required for sum")
    private BigDecimal price;

    @NotNull(message = "Image is required for sum")
    private String image;

    @NotNull(message = "Size is required for sum")
    private Size size;

}
