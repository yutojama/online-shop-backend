package com.portshop.order_service.controller;

import com.portshop.order_service.dto.AddressRequestDTO;
import com.portshop.order_service.dto.OrderShippingInfoRequestDTO;
import com.portshop.order_service.model.Address;
import com.portshop.order_service.model.Cart;
import com.portshop.order_service.model.Order;
import com.portshop.order_service.orderEnum.Carrier;
import com.portshop.order_service.service.CartService;
import com.portshop.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;

    public OrderController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            Authentication authentication,
            @Valid @RequestBody OrderShippingInfoRequestDTO orderShippingInfoRequestDTO
            ) {

        String kcId = authentication.getName();
        Cart cart = cartService.getCart(kcId);
        if(cart.getCartItems().isEmpty()){
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        Carrier carrier = orderShippingInfoRequestDTO.getCarrier();
        AddressRequestDTO shippingAddressDTO =  orderShippingInfoRequestDTO.getShippingAddress();
        AddressRequestDTO billingAddressDTO =  orderShippingInfoRequestDTO.getBillingAddress();

        Address shippingAddress = Address.builder()
                .firstName(shippingAddressDTO.getFirstName())
                .lastName(shippingAddressDTO.getLastName())
                .street(shippingAddressDTO.getStreet())
                .city(shippingAddressDTO.getCity())
                .postalCode(shippingAddressDTO.getPostalCode())
                .country(shippingAddressDTO.getCountry())
                .build();

        Address billingAddress = Address.builder()
                .firstName(billingAddressDTO.getFirstName())
                .lastName(billingAddressDTO.getLastName())
                .street(billingAddressDTO.getStreet())
                .city(billingAddressDTO.getCity())
                .postalCode(billingAddressDTO.getPostalCode())
                .country(billingAddressDTO.getCountry())
                .build();

        Order order = orderService.createOrderFromCart(cart, carrier, shippingAddress, billingAddress);
        Order orderWithHistory = orderService.createOrderHistory(order);

        // clear all items in cart
        cartService.clearCart(cart);

        return ResponseEntity.ok().body(orderWithHistory);
//        return ResponseEntity.ok().body("Order created successfully");

    }



}
