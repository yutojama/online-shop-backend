package com.portshop.payment_service.dto.orderDTO;

import com.portshop.payment_service.paymentEnum.Size;
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

}
