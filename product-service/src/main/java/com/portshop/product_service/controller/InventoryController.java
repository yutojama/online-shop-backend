package com.portshop.product_service.controller;

import com.portshop.product_service.dto.orderDTO.ItemReservationDTO;
import com.portshop.product_service.dto.orderDTO.OrderDTO;
import com.portshop.product_service.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inv/items")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
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

}
