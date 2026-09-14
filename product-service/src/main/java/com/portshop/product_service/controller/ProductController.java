package com.portshop.product_service.controller;

import com.portshop.product_service.dto.ProductMapper;
import com.portshop.product_service.dto.ProductRequestDTO;
import com.portshop.product_service.dto.ProductResponseDTO;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.service.ProductService;
import com.portshop.product_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final UserService userService;

    public ProductController(ProductService productService, ProductMapper productMapper, UserService userService) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.userService = userService;
    }



    /**
     * customer & shop handling from here
     * */

    @PreAuthorize("hasAnyRole('SHOP')")
    @GetMapping("/get-prods/{shopId}")
    public ResponseEntity<?> getProducts(
            @PathVariable String shopId,
            Authentication authentication
    ){
        String kcId = authentication.getName();
        List<Product> products = productService.getProductsByShopId(shopId);
        List<ProductResponseDTO> productResponseDTOList = products.stream().map(productMapper::ProductToProductResponseDTO).toList();
        return ResponseEntity.ok().body(productResponseDTOList);
    }

    // deprecated, will be deleted
    @PreAuthorize("hasAnyRole('SHOP')")
    @PostMapping("/create-prod")
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO,
            Authentication authentication
    ){

        // get keycloak id from auth context
        String kcId = authentication.getName();
        Product newProduct = productService.createProduct(productMapper.ProductRequestDTOToProduct(productRequestDTO, kcId));
        ProductResponseDTO productResponseDTO = productMapper.ProductToProductResponseDTO(newProduct);
        return ResponseEntity.ok().body(productResponseDTO);

    }

    @PreAuthorize("hasAnyRole('SHOP')")
    @PostMapping("/create-prods")
    public ResponseEntity<?> createProducts(
            @Valid @RequestBody List<ProductRequestDTO> productRequestDTO,
            Authentication authentication
    ){

        // get keycloak id from auth context
        String kcId = authentication.getName();

        for (ProductRequestDTO prod : productRequestDTO) {
            productService.createProduct(productMapper.ProductRequestDTOToProduct(prod, kcId));
        }
        return ResponseEntity.ok().body("Create products successful");

    }

    @PreAuthorize("hasAnyRole('SHOP')")
    @DeleteMapping("/delete-prod/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable String id,
            Authentication authentication
    ){
        String kcId = authentication.getName();
        UUID prodId = UUID.fromString(id);
        if(productService.verifyProduct(prodId, kcId)){
            productService.deleteProductById(prodId);
            return ResponseEntity.ok().body("Delete successful");
        }
        return ResponseEntity.ok().body("Delete failed: verify product failed");
    }



    /**
     * admin handling from here
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-all")
    public ResponseEntity<?> deleteAllProducts(){
        productService.deleteAllProducts();
        return ResponseEntity.ok().body("Delete all successful");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/admin-delete-prod/{id}")
    public ResponseEntity<?> adminDeleteProduct(@PathVariable String id){
        productService.deleteProductById(UUID.fromString(id));
        return ResponseEntity.ok().body("Delete successful");
    }

    /**
     * reason of taking keycloakId as product-user-recognition:
     * jwt contains keycloakId. If using userId. If product carries kcId, product-service could recognize belongingness itself,
     * a request on user-service is avoided
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/import-data")
    public ResponseEntity<?> importAll(@Valid @RequestBody List<ProductRequestDTO> productRequestDTO){
        List<String> kcIdList = userService.getUserIdByRole("SHOP");

        for (ProductRequestDTO prod : productRequestDTO) {
            String kcId = kcIdList.get(ThreadLocalRandom.current().nextInt(kcIdList.size()));
            productService.createProduct(productMapper.ProductRequestDTOToProduct(prod, kcId));
        }
        return ResponseEntity.ok().body("Create successful");
    }

}
