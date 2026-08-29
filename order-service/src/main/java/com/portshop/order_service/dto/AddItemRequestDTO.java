package com.portshop.order_service.dto;

import com.portshop.order_service.orderEnum.Size;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddItemRequestDTO {

    @NotNull(message = "Quantity is required for adding item")
    private Integer quantity;

    @NotNull(message = "Size is required for adding item")
    private Size size;

    @NotBlank(message = "ItemId is required for adding item")
    private String itemId;

    @NotBlank(message = "ProductId is required for adding item")
    private String productId;

    @NotBlank(message = "ProductName is required for adding item")
    private String productName;

    @NotBlank(message = "Image url is required for adding item")
    private String image;

    @NotNull(message = "UnitPrice is required for adding item")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal unitPrice;

    @Digits(integer = 8, fraction = 2)
    private BigDecimal discountPrice;

}
