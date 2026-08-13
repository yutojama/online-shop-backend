package com.portshop.product_service.dto;

import com.portshop.product_service.productEnum.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDTO {
    private String id;
    private String productId;
    private Integer quantity;
    private Size size;
}
