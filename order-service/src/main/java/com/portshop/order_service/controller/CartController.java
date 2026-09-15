package com.portshop.order_service.controller;

import com.portshop.order_service.dto.*;
import com.portshop.order_service.model.Cart;
import com.portshop.order_service.model.CartItem;
import com.portshop.order_service.orderEnum.Size;
import com.portshop.order_service.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /** default handling, should be performed after account is created */
    @PostMapping("/create")
    public ResponseEntity<?> createCart(
            Authentication authentication
    ) {
        String kcId = authentication.getName();
        Cart cart = cartService.createCart(kcId);
        return ResponseEntity.ok().body("Cart id: "+cart.getId());
    }

    @GetMapping("/get-cart")
    public ResponseEntity<?> getCart(
            Authentication authentication
    ){
        String kcId = authentication.getName();
        Cart cart = cartService.createCart(kcId);
        if(cart.getCartItems().isEmpty()){
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        // create response dto
        GetCartResponseDTO getCartResponseDTO = new GetCartResponseDTO();
        List<CartItem> cartItems = cart.getCartItems();
        cartItems.sort(Comparator.comparing(CartItem::getItemId));
        cartItems.forEach(cartItem -> {
            GetCartItemResponseDTO getCartItemResponseDTO = GetCartItemResponseDTO.builder()
                    .id(cartItem.getId().toString())
                    .shopId(cartItem.getShopId())
                    .quantity(cartItem.getQuantity())
                    .size(cartItem.getSize())
                    .itemId(cartItem.getItemId())
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .image(cartItem.getImage())
                    .unitPrice(cartItem.getUnitPrice())
                    .discountPrice(cartItem.getDiscountPrice())
                    .daysOfDelivery(cartItem.getDaysOfDelivery())
                    .available(cartItem.getAvailable())
                    .build();
            getCartResponseDTO.getCartItemResponseDTOList().add(getCartItemResponseDTO);
        });
        CartSumResponseDTO cartSumDTO = cartService.getCartSum(cart);
        getCartResponseDTO.setCartSumResponseDTO(cartSumDTO);

        return ResponseEntity.ok().body(getCartResponseDTO);
    }

    @PostMapping("/add-item")
    public ResponseEntity<?> addItemToCart(
            @Valid @RequestBody AddItemRequestDTO addItemRequestDTO,
            Authentication authentication
    ) {
        String kcId = authentication.getName();
        Cart cart = cartService.getCart(kcId);

        CartItem cartItem = CartItem.builder()
                .shopId(addItemRequestDTO.getShopId())
                .quantity(addItemRequestDTO.getQuantity())
                .size(addItemRequestDTO.getSize())
                .itemId(addItemRequestDTO.getItemId())
                .productId(addItemRequestDTO.getProductId())
                .productName(addItemRequestDTO.getProductName())
                .image(addItemRequestDTO.getImage())
                .unitPrice(addItemRequestDTO.getUnitPrice())
                .discountPrice(addItemRequestDTO.getDiscountPrice())
                .daysOfDelivery(addItemRequestDTO.getDaysOfDelivery())
                .available(addItemRequestDTO.getAvailable())
                .build();
        Cart newCart = cartService.addItemToCart(cart, cartItem);


        return ResponseEntity.ok().body(cartService.getCartSum(newCart));
    }

    @GetMapping("/get-items")
    public ResponseEntity<?> getCartItems(
            Authentication authentication
    ){
        String kcId = authentication.getName();
        Cart cart = cartService.getCart(kcId);
        List<CartItem> cartItems = cart.getCartItems();
        List<CartItemResponseDTO> cartItemResponseDTOList = new ArrayList<>();
        cartItems.forEach(cartItem -> cartItemResponseDTOList.add(
           CartItemResponseDTO.builder()
                   .productId(cartItem.getProductId())
                   .itemId(cartItem.getItemId())
                   .productName(cartItem.getProductName())
                   .image(cartItem.getImage())
                   .unitPrice(cartItem.getUnitPrice())
                   .discountPrice(cartItem.getDiscountPrice())
                   .quantity(cartItem.getQuantity())
                   .size(cartItem.getSize())
                   .build()
        ));
        return ResponseEntity.ok().body(cartItemResponseDTOList);
    }

    @DeleteMapping("/delete-item/{itemId}")
    public ResponseEntity<?> deleteItemFromCart(
            @PathVariable String itemId,
            Authentication authentication
    ){
        String kcId = authentication.getName();
        Cart cart = cartService.getCart(kcId);
        Cart newCart = cartService.removeItemFromCart(cart,  itemId);

        return ResponseEntity.ok().body(cartService.getCartSum(newCart));
    }

    @PatchMapping("/plus-item/{itemId}")
    public ResponseEntity<?> plusItemToCart(
            @PathVariable String itemId,
            Authentication authentication
    ){
        String kcId = authentication.getName();
        Cart cart = cartService.getCart(kcId);
        Cart newCart = cartService.plusItemToCart(cart, itemId);

        return ResponseEntity.ok().body(cartService.getCartSum(newCart));
    }

    @PatchMapping("/minus-item/{itemId}")
    public ResponseEntity<?> minusItemToCart(
            @PathVariable String itemId,
            Authentication authentication
    ){
        String kcId = authentication.getName();
        Cart cart = cartService.getCart(kcId);
        Cart newCart = cartService.minusItemToCart(cart, itemId);

        return ResponseEntity.ok().body(cartService.getCartSum(newCart));
    }

    @DeleteMapping("/clear-cart")
    public ResponseEntity<?> clearCart(Authentication authentication){
        String kcId = authentication.getName();
        Cart cart = cartService.getCart(kcId);
        cartService.clearCart(cart);
        return ResponseEntity.ok().body("Cart cleared successfully");
    }

}
