package com.portshop.product_service.dto;

import com.portshop.product_service.model.Item;
import com.portshop.product_service.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {
    public ItemResponseDTO toItemResponseDTO(Item item) {
        return ItemResponseDTO.builder()
                .id(item.getId().toString())
                .productId(item.getProduct().getId().toString())
                .quantity(item.getQuantity())
                .size(item.getSize())
                .build();
    }
}
