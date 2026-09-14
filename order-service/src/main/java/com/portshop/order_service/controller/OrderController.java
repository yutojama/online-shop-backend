package com.portshop.order_service.controller;

import com.portshop.order_service.dto.AddressRequestDTO;
import com.portshop.order_service.dto.OrderShippingInfoRequestDTO;
import com.portshop.order_service.dto.orderDTO.ItemReservationDTO;
import com.portshop.order_service.dto.orderDTO.OrderDTO;
import com.portshop.order_service.exception.ItemReservationException;
import com.portshop.order_service.model.*;
import com.portshop.order_service.orderEnum.Carrier;
import com.portshop.order_service.orderEnum.OrderStatus;
import com.portshop.order_service.orderEnum.PaymentMethod;
import com.portshop.order_service.orderEnum.PaymentStatus;
import com.portshop.order_service.repository.ItemChangeRecordRepository;
import com.portshop.order_service.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final KafkaOrderProducerService kafkaOrderProducerService;
    private final InventoryService inventoryService;
    private final ItemChangeRecordRepository itemChangeRecordRepository;
    private final ItemChangeRecordService itemChangeRecordService;

    public OrderController(CartService cartService, OrderService orderService, KafkaOrderProducerService kafkaOrderProducerService, InventoryService inventoryService, ItemChangeRecordRepository itemChangeRecordRepository, ItemChangeRecordService itemChangeRecordService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.kafkaOrderProducerService = kafkaOrderProducerService;
        this.inventoryService = inventoryService;
        this.itemChangeRecordRepository = itemChangeRecordRepository;
        this.itemChangeRecordService = itemChangeRecordService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/get-order")
    public ResponseEntity<?> getOrder(
            Authentication authentication
    ) {
        String kcId = authentication.getName();
        List<Order> orders = orderService.getOrdersByUserKeycloakId(kcId);
        if (orders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<OrderDTO> orderDTOs = orders.stream().map(orderService::getOrderDTO).toList();
        return ResponseEntity.ok(orderDTOs);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/create")
    @Transactional
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

        // create item history for shop searching
        itemChangeRecordService.createItemHistoryByOrder(orderWithHistory);

        // create order DTO
        OrderDTO orderDTO = orderService.getOrderDTO(orderWithHistory);

        // check inventory
        List<ItemReservationDTO> itemReservationDTOs = inventoryService.reserveItem(orderDTO);
        List<String> messageList = itemReservationDTOs.stream().map(ItemReservationDTO::getMessage).toList();
        boolean ifReject = messageList.stream().anyMatch(message->message.contains("REJECTED"));
        if(ifReject){
            throw new ItemReservationException("Item reservation rejected", itemReservationDTOs);
        }

        // publish order for payment
        kafkaOrderProducerService.publishForOrderSubmit(orderDTO);

        // clear all items in cart
        cartService.clearCart(cart);

        return ResponseEntity.ok().body(orderDTO);

    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(
            @PathVariable String orderId,
            Authentication authentication
    ) {
        Order order = orderService.getOrder(orderId);
        String kcId = authentication.getName();
        orderService.checkOrderAuth(order, kcId);
        Order savedOrder = orderService.cancelOrderHistory(order, OrderStatus.PENDING);

        // add item history for shop searching
        itemChangeRecordService.addItemHistoryByOrder(
                savedOrder,
                OrderStatus.PENDING,
                OrderStatus.CANCELLED,
                "Customer",
                "Item order cancelled"
        );

        String cancelMessage =  inventoryService.cancelItemReservation(savedOrder.getId().toString());
        kafkaOrderProducerService.publishForOrderSubmit(
                OrderDTO.builder()
                        .orderStatus(OrderStatus.CANCELLED)
                        .paymentStatus(PaymentStatus.CANCELED)
                        .orderId(orderId)
                        .build()
        );
        return ResponseEntity.ok().body(cancelMessage + ", orderId: "+orderId);
    }

}
