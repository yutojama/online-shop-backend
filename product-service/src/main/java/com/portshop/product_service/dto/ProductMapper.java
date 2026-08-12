package com.portshop.product_service.dto;

import com.portshop.product_service.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product ProductRequestDTOToProduct(ProductRequestDTO productRequestDTO){
        return Product.builder()
                .brand(productRequestDTO.getBrand())
                .category(productRequestDTO.getCategory().toString())
                .gender(productRequestDTO.getGender().toString())
                .description(productRequestDTO.getDescription())
                .amountRemain(productRequestDTO.getAmountRemain())
                .daysOfDelivery(productRequestDTO.getDaysOfDelivery())
                .origPrice(productRequestDTO.getOrigPrice())
                .discountPrice(productRequestDTO.getDiscountPrice())
                .image(productRequestDTO.getImage())
                .build();
    }

    public ProductResponseDTO ProductToProductResponseDTO(Product product){
        return ProductResponseDTO.builder()
                .id(product.getId())
                .brand(product.getBrand())
                .category(product.getCategory())
                .gender(product.getGender())
                .description(product.getDescription())
                .amountRemain(product.getAmountRemain())
                .daysOfDelivery(product.getDaysOfDelivery())
                .origPrice(product.getOrigPrice())
                .discountPrice(product.getDiscountPrice())
                .image(product.getImage())
                .build();
    }

}
