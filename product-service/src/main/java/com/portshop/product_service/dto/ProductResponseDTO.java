package com.portshop.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private UUID id;
    private String shopId;
    private String brand;
    private String category;
    private String gender;
    private String description;
    private Integer quantity;
    private Integer daysOfDelivery;
    private Integer origPrice;
    private Integer discountPrice;
    private String image;

}
