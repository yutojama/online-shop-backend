package com.portshop.order_service.dto.orderDTO;

import com.portshop.order_service.orderEnum.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {

    private String itemId;
    private String productId;
    private String productName;
    private Integer quantity;
    private Size size;
    private BigDecimal unitPrice;
    private BigDecimal discountPrice;
    private String image;

}
