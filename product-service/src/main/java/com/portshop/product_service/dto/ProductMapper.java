package com.portshop.product_service.dto;

import com.portshop.product_service.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product ProductRequestDTOToProduct(ProductRequestDTO productRequestDTO){
        return Product.builder()
                .brand(productRequestDTO.getBrand())
                .category(productRequestDTO.getCategory())
                .gender(productRequestDTO.getGender())
                .description(productRequestDTO.getDescription())
                .quantity(productRequestDTO.getQuantity())
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
                .category(product.getCategory().toString())
                .gender(product.getGender().toString())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .daysOfDelivery(product.getDaysOfDelivery())
                .origPrice(product.getOrigPrice())
                .discountPrice(product.getDiscountPrice())
                .image(product.getImage())
                .build();
    }

}
