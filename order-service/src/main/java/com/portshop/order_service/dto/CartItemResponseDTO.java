package com.portshop.order_service.dto;

import com.portshop.order_service.orderEnum.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDTO {
    private String productId;
    private String itemId;
    private String productName;
    private String image;
    private BigDecimal unitPrice;
    private BigDecimal discountPrice;
    private Integer quantity;
    private Size size;
}
