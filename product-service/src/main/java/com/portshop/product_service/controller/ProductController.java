package com.portshop.product_service.controller;

import com.portshop.product_service.dto.ProductMapper;
import com.portshop.product_service.dto.ProductRequestDTO;
import com.portshop.product_service.dto.ProductResponseDTO;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO){
        Product newProduct = productService.createProduct(productMapper.ProductRequestDTOToProduct(productRequestDTO));
        log.debug("REST request to save Product : {}", newProduct);
        ProductResponseDTO productResponseDTO = productMapper.ProductToProductResponseDTO(newProduct);
        return ResponseEntity.ok().body(productResponseDTO);
    }

    @PostMapping("/create-prods")
    public ResponseEntity<?> createProducts(@Valid @RequestBody List<ProductRequestDTO> productRequestDTO){
        for (ProductRequestDTO prod : productRequestDTO) {
            productService.createProduct(productMapper.ProductRequestDTOToProduct(prod));
        }
        return ResponseEntity.ok().body("Create successful");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable String id){
        Product product = productService.getProductById(UUID.fromString(id));
        ProductResponseDTO productResponseDTO = productMapper.ProductToProductResponseDTO(product);
        return ResponseEntity.ok().body(productResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id){
        productService.deleteProductById(UUID.fromString(id));
        return ResponseEntity.ok().body("Delete successful");
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<?> deleteAllProducts(){
        productService.deleteAllProducts();
        return ResponseEntity.ok().body("Delete all successful");
    }

}
