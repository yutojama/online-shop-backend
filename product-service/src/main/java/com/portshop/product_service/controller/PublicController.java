package com.portshop.product_service.controller;

import com.portshop.product_service.dto.ItemMapper;
import com.portshop.product_service.dto.ItemResponseDTO;
import com.portshop.product_service.dto.ProductMapper;
import com.portshop.product_service.dto.ProductResponseDTO;
import com.portshop.product_service.model.Item;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.productEnum.Category;
import com.portshop.product_service.productEnum.Gender;
import com.portshop.product_service.productEnum.Size;
import com.portshop.product_service.service.ItemService;
import com.portshop.product_service.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                productService.findAllByOrderByCreatedAtAsc().stream().map(productMapper::ProductToProductResponseDTO).toList();
        return ResponseEntity.ok(productResponseDTOList);
    }

    @GetMapping("/products/get-prod/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable String id){
        Product product = productService.getProductById(UUID.fromString(id));
        ProductResponseDTO productResponseDTO = productMapper.ProductToProductResponseDTO(product);
        return ResponseEntity.ok().body(productResponseDTO);
    }

    @GetMapping("/products/get-filter-prods")
    public ResponseEntity<?> getFilteredProducts(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String cat,
            @RequestParam(required = false) String size, // not yet implemented
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice
    ) {
        Gender genderEnum = gender != null ? Gender.valueOf(gender) : null;
        Category catEnum = cat != null ? Category.valueOf(cat) : null;
        Size sizeEnum = size != null ? Size.valueOf(size) : null;
        List<Product> products = productService.getFilteredProducts(genderEnum,catEnum,sizeEnum,minPrice,maxPrice);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/search/{keyword}")
    public ResponseEntity<?> searchProducts(@PathVariable String keyword){
        List<Product> products = productService.getSearchedProducts(keyword);
        return ResponseEntity.ok(products);
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
