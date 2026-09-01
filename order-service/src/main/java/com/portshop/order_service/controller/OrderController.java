package com.portshop.order_service.controller;

import com.portshop.order_service.dto.AddressRequestDTO;
import com.portshop.order_service.dto.OrderShippingInfoRequestDTO;
import com.portshop.order_service.dto.orderDTO.OrderDTO;
import com.portshop.order_service.model.*;
import com.portshop.order_service.orderEnum.Carrier;
import com.portshop.order_service.orderEnum.PaymentMethod;
import com.portshop.order_service.service.CartService;
import com.portshop.order_service.service.KafkaOrderProducerService;
import com.portshop.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final KafkaOrderProducerService kafkaOrderProducerService;

    public OrderController(CartService cartService, OrderService orderService, KafkaOrderProducerService kafkaOrderProducerService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.kafkaOrderProducerService = kafkaOrderProducerService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            Authentication authentication,
            @Valid @RequestBody OrderShippingInfoRequestDTO orderShippingInfoRequestDTO
            ) {

        String kcId = authentication.getName();
        Cart cart = cartService.getCart(kcId);

        // return null if cart is empty
        if(cart.getCartItems().isEmpty()){
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        // create order
        Carrier carrier = orderShippingInfoRequestDTO.getCarrier();
        PaymentMethod paymentMethod = orderShippingInfoRequestDTO.getPaymentMethod();
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

        Order order = orderService.createOrderFromCart(cart, paymentMethod, carrier, shippingAddress, billingAddress);

        // create shipments
        Order orderWithShipment = orderService.createOrderShipment(order);

        // create history
        Order orderWithHistory = orderService.createOrderHistory(orderWithShipment);

        OrderDTO orderDTO = orderService.getOrderDTO(orderWithHistory);

        // publish order for payment
        kafkaOrderProducerService.publishForOrderSubmit(orderDTO);

        // clear all items in cart
        cartService.clearCart(cart);
        return ResponseEntity.ok().body(orderDTO);

    }



}
