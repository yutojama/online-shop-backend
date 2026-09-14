package com.portshop.product_service.controller;

import com.portshop.product_service.dto.ItemMapper;
import com.portshop.product_service.dto.ItemResponseDTO;
import com.portshop.product_service.dto.ProductMapper;
import com.portshop.product_service.dto.ProductResponseDTO;
import com.portshop.product_service.model.Item;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.service.ItemService;
import com.portshop.product_service.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final ProductService productService;
    private final ItemService itemService;
    private final ProductMapper productMapper;
    private final ItemMapper itemMapper;

    public PublicController(ProductService productService, ItemService itemService, ProductMapper productMapper, ItemMapper itemMapper) {
        this.productService = productService;
        this.itemService = itemService;
        this.productMapper = productMapper;
        this.itemMapper = itemMapper;
    }


    /**
     * guest product handling from here, accessible without login possible
     * */
    @GetMapping("/products/get-prods")
    public ResponseEntity<?> getProducts(){
        List<ProductResponseDTO> productResponseDTOList =
                productService.findAll().stream().map(productMapper::ProductToProductResponseDTO).toList();
        return ResponseEntity.ok(productResponseDTOList);
    }

    @GetMapping("/products/get-prod/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable String id){
        Product product = productService.getProductById(UUID.fromString(id));
        ProductResponseDTO productResponseDTO = productMapper.ProductToProductResponseDTO(product);
        return ResponseEntity.ok().body(productResponseDTO);
    }



    /**
     * guest item handling from here, accessible without login possible
     * */
    @GetMapping("/items/get-prod-items/{prodId}")
    public ResponseEntity<?> getProdItems(@PathVariable String prodId){
        Product product = productService.getProductById(UUID.fromString(prodId));
        List<ItemResponseDTO> itemDTOs = product.getItems().stream().map(itemMapper::toItemResponseDTO).toList();
        return  ResponseEntity.ok().body(itemDTOs);
    }

    @GetMapping("/items/get-item/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable String itemId){
        Item item = itemService.getItem(UUID.fromString(itemId));
        ItemResponseDTO itemResponseDTO = itemMapper.toItemResponseDTO(item);
        return  ResponseEntity.ok().body(itemResponseDTO);
    }

}
