package com.portshop.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartSumResponseDTO {

    private List<ItemShortResponseDTO> itemList;
    private BigDecimal subTotal;
    private BigDecimal discount;
    private BigDecimal total;
    private BigDecimal tax;
    private BigDecimal shippingCost;

}
