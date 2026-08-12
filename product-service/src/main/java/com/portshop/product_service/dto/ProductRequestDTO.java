package com.portshop.product_service.dto;

import com.portshop.product_service.productEnum.Category;
import com.portshop.product_service.productEnum.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Description is required")
    private String description;

    private Integer amountRemain;
    private Integer daysOfDelivery;

    @NotNull(message = "Price is required")
    private Integer origPrice;

    private Integer discountPrice;

    @NotBlank(message = "Image is required")
    private String image;

}
