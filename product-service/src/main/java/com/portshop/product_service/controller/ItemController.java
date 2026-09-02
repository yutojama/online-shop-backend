package com.portshop.product_service.controller;

import com.portshop.product_service.dto.ItemImportRequestDTO;
import com.portshop.product_service.dto.ItemMapper;
import com.portshop.product_service.dto.ItemRequestDTO;
import com.portshop.product_service.dto.ItemResponseDTO;
import com.portshop.product_service.dto.orderDTO.ItemReservationDTO;
import com.portshop.product_service.dto.orderDTO.OrderDTO;
import com.portshop.product_service.model.Item;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.service.InventoryService;
import com.portshop.product_service.service.ItemService;
import com.portshop.product_service.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final ItemMapper itemMapper;
    private final ItemService itemService;
    private final ProductService productService;
    private final InventoryService inventoryService;

    public ItemController(ItemMapper itemMapper, ItemService itemService, ProductService productService, InventoryService inventoryService) {
        this.itemMapper = itemMapper;
        this.itemService = itemService;
        this.productService = productService;
        this.inventoryService = inventoryService;
    }

    // deprecated, will be deleted later
    @PreAuthorize("hasAnyRole('ADMIN','SHOP')")
    @PostMapping("/add-item")
    public ResponseEntity<?> addItem(@Valid @RequestBody ItemRequestDTO itemRequestDTO){
        Item item = Item.builder()
                .quantity(itemRequestDTO.getQuantity())
                .size(itemRequestDTO.getSize())
                .product(productService.getProductById(UUID.fromString(itemRequestDTO.getProductId())))
                .build();
        Item savedItem = itemService.addItem(item);
        return ResponseEntity.ok().body(itemMapper.toItemResponseDTO(savedItem));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SHOP')")
    @PostMapping("/add-items")
    public ResponseEntity<?> addItems(@RequestBody List<ItemRequestDTO> itemRequestDTOs){
        List<Item> items = itemRequestDTOs.stream().map(itemRequestDTO ->
                Item.builder()
                    .quantity(itemRequestDTO.getQuantity())
                    .size(itemRequestDTO.getSize())
                    .product(productService.getProductById(UUID.fromString(itemRequestDTO.getProductId())))
                    .build()
        ).toList();
        itemService.addItems(items);
        return ResponseEntity.ok().body("Add items successfully");
    }

    @GetMapping("/get-item/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable String itemId){
        Item item = itemService.getItem(UUID.fromString(itemId));
        ItemResponseDTO itemResponseDTO = itemMapper.toItemResponseDTO(item);
        return  ResponseEntity.ok().body(itemResponseDTO);
    }




    /**
     * inventory controller from here
     * */
    @PostMapping("/reserve-item")
    public ResponseEntity<?> reserveItem(
            @RequestBody OrderDTO orderDTO
    ){
        List<ItemReservationDTO> itemReservationDTOs = inventoryService.reserveItem(orderDTO);
        return ResponseEntity.ok().body(itemReservationDTOs);
    }

    @GetMapping("/cancel-item/{orderId}")
    public ResponseEntity<?> cancelItem(
            @PathVariable String orderId
    ){
        inventoryService.cancelItemReservation(orderId);
        return ResponseEntity.ok().body("Cancel item reservation successfully");
    }



    /**
     * data handling from here
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/import-data")
    public ResponseEntity<?> importData(@RequestBody List<ItemImportRequestDTO> itemImportRequestDTOS){
        List<Product> products = productService.findAllByOrderByCreatedAtAsc();
        logger.debug(products.stream().map(Product::getBrand).toList().toString());
        List<Item> items = itemImportRequestDTOS.stream().map(it -> Item.builder()
                .quantity(it.getQuantity())
                .size(it.getSize())
                .product(products.get(it.getProductId()))
                .build()).toList();
        itemService.addItems(items);
        return ResponseEntity.ok().body("Import data successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-all")
    public ResponseEntity<?> deleteAll(){
        itemService.deleteAll();
        return ResponseEntity.ok().body("Delete all items successfully");
    }

}
