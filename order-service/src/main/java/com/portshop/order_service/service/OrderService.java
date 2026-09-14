package com.portshop.order_service.service;

import com.portshop.order_service.dto.AddressRequestDTO;
import com.portshop.order_service.dto.CartSumResponseDTO;
import com.portshop.order_service.dto.orderDTO.OrderDTO;
import com.portshop.order_service.dto.orderDTO.OrderItemDTO;
import com.portshop.order_service.dto.orderDTO.OrderShipmentDTO;
import com.portshop.order_service.exception.OrderAuthException;
import com.portshop.order_service.exception.OrderNotFoundException;
import com.portshop.order_service.model.*;
import com.portshop.order_service.orderEnum.*;
import com.portshop.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }


    public Order getOrder(String orderId) {
        Order order = orderRepository.getOrderById(UUID.fromString(orderId));
        if(order == null){
            throw new OrderNotFoundException(orderId);
        }
        return order;
    }

    public Order createOrderFromCart(Cart cart,
                                    PaymentMethod paymentMethod,
                                    Carrier carrier,
                                    Address shippingAddress,
                                    Address billingAddress
    ) {

        // get cart items and cart summary
        List<CartItem> cartItems = cart.getCartItems();
        CartSumResponseDTO cartSum = cartService.getCartSum(cart);

        // create order
        Order order = Order.builder()
                .userKeycloakId(cart.getUserKeycloakId())
                .subtotal(cartSum.getSubTotal())
                .total(cartSum.getTotal())
                .tax(cartSum.getTax())
                .shippingCost(cartSum.getShippingCost())
                .paymentMethod(paymentMethod)
                .carrier(carrier)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .build();

        // add items from cart to order
        cartItems
                .forEach(cartItem ->
                    order.addOrderItem(
                            OrderItem.builder()
                                    .order(order)
                                    .shopKeycloakId(cartItem.getShopKeycloakId())
                                    .quantity(cartItem.getQuantity())
                                    .size(cartItem.getSize())
                                    .itemId(cartItem.getItemId())
                                    .productId(cartItem.getProductId())
                                    .productName(cartItem.getProductName())
                                    .image(cartItem.getImage())
                                    .unitPrice(cartItem.getUnitPrice())
                                    .discountPrice(cartItem.getDiscountPrice())
                                    .build()
                    )
                );
        return orderRepository.save(order);

    }

    public Order createOrderShipment(Order order) {
        List<Shipment> shipments = order.getShipments();
        order.getOrderItems().forEach(orderItem -> {
            // check if shipment with shop id already exists
            Shipment existShipment = shipments.stream()
                    .filter(spm ->
                            spm.getShopKeycloakId()
                                    .equals(orderItem.getShopKeycloakId())
                    ).findFirst()
                    .orElse(null);

            if(existShipment == null){
                Shipment shipment = Shipment.builder()
                        .shopKeycloakId(orderItem.getShopKeycloakId())
                        .carrier(order.getCarrier())
                        .order(order)
                        .build();
                shipment.addOrderItem(orderItem);
                shipments.add(shipment);
            } else {
                existShipment.addOrderItem(orderItem);
            }
        });

        // set shipment id for orderItems
        Order newOrder = orderRepository.save(order);
        order.getShipments().forEach(shipment -> {
            shipment.getOrderItems().forEach(orderItem -> {
                orderItem.setShipment(shipment);
            });
        });

        return orderRepository.save(newOrder);
    }

    /**
     * order db handling from here
     **/
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public void checkOrderAuth(Order order, String keycloakId) {
        if(!order.getUserKeycloakId().equals(keycloakId)){
            throw new OrderAuthException(" order id: "+order.getId());
        }
    }


    /**
     * methods for creating history logging and save order from here
     **/

    public Order saveOrderWithLog(Order order, String changedBy, String reason){
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(order.getOrderStatus())
                        .toStatus(order.getOrderStatus())
                        .changedAt(Instant.now())
                        .changedBy(changedBy)
                        .reason(reason)
                        .build()
        );
        return orderRepository.save(order);
    }

    public Order createOrderHistory(Order order) {
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .toStatus(OrderStatus.PENDING)
                        .changedAt(Instant.now())
                        .changedBy("Customer")
                        .reason("Order created")
                        .build()
        );
        return orderRepository.save(order);
    }

    public Order confirmOrderHistory(Order order) {
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(OrderStatus.PENDING)
                        .toStatus(OrderStatus.CONFIRMED)
                        .changedAt(Instant.now())
                        .changedBy("Payment service")
                        .reason("Payment successful")
                        .build()
        );
        return orderRepository.save(order);
    }

    public Order paymentFailedHistory(Order order) {
        order.setOrderStatus(OrderStatus.PENDING);
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(OrderStatus.PENDING)
                        .toStatus(OrderStatus.PENDING)
                        .changedAt(Instant.now())
                        .changedBy("Payment service")
                        .reason("Payment failed")
                        .build()
        );
        return orderRepository.save(order);
    }

    public Order processOrderHistory(Order order) {
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(OrderStatus.CONFIRMED)
                        .toStatus(OrderStatus.PROCESSING)
                        .changedAt(Instant.now())
                        .changedBy("Shipping service")
                        .reason("Order in process")
                        .build()
        );
        return orderRepository.save(order);
    }

    public Order shippingOrderHistory(Order order) {
        order.setOrderStatus(OrderStatus.SHIPPED);
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(OrderStatus.PROCESSING)
                        .toStatus(OrderStatus.SHIPPED)
                        .changedAt(Instant.now())
                        .changedBy("Shipping service")
                        .reason("Order in shipment")
                        .build()
        );
        return orderRepository.save(order);
    }

    public Order deliverOrderHistory(Order order) {
        order.setOrderStatus(OrderStatus.DELIVERED);
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(OrderStatus.SHIPPED)
                        .toStatus(OrderStatus.DELIVERED)
                        .changedAt(Instant.now())
                        .changedBy("Shipping service")
                        .reason("Order delivered")
                        .build()
        );
        return orderRepository.save(order);
    }

    public Order cancelOrderHistory(Order order, OrderStatus fromStatus) {
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.CANCELED);
        order.getShipments().forEach(shipment -> {
            shipment.setShipmentStatus(ShipmentStatus.CANCELLED);
        });
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(fromStatus)
                        .toStatus(OrderStatus.CANCELLED)
                        .changedAt(Instant.now())
                        .changedBy("Customer")
                        .reason("Order cancelled")
                        .build()
        );
        return orderRepository.save(order);
    }

    public OrderDTO getOrderDTO(Order order) {
        OrderDTO orderDTO = OrderDTO.builder()
                .orderId(order.getId().toString())
                .userKeycloakId(order.getUserKeycloakId())
                .orderStatus(order.getOrderStatus())
                .subtotal(order.getSubtotal())
                .total(order.getTotal())
                .tax(order.getTax())
                .shippingCost(order.getShippingCost())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .carrier(order.getCarrier())
                .billingAddress(AddressRequestDTO.builder()
                        .firstName(order.getBillingAddress().getFirstName())
                        .lastName(order.getBillingAddress().getLastName())
                        .street(order.getBillingAddress().getStreet())
                        .city(order.getBillingAddress().getCity())
                        .postalCode(order.getBillingAddress().getPostalCode())
                        .country(order.getBillingAddress().getCountry())
                        .build()
                )
                .shippingAddress(AddressRequestDTO.builder()
                        .firstName(order.getShippingAddress().getFirstName())
                        .lastName(order.getShippingAddress().getLastName())
                        .street(order.getShippingAddress().getStreet())
                        .city(order.getShippingAddress().getCity())
                        .postalCode(order.getShippingAddress().getPostalCode())
                        .country(order.getShippingAddress().getCountry())
                        .build()
                )
                .build();

        order.getShipments().forEach(shipment -> {
            OrderShipmentDTO orderShipmentDTO = OrderShipmentDTO.builder()
                    .shipmentId(shipment.getId().toString())
                    .shopKeycloakId(shipment.getShopKeycloakId())
                    .carrier(shipment.getCarrier())
                    .trackingNumber(shipment.getTrackingNumber())
                    .shipmentStatus(shipment.getShipmentStatus())
                    .build();
            shipment.getOrderItems().forEach(orderItem ->
                    orderShipmentDTO.addOrderItemDTO(
                            OrderItemDTO.builder()
                            .itemId(orderItem.getItemId())
                            .productId(orderItem.getProductId())
                            .productName(orderItem.getProductName())
                            .quantity(orderItem.getQuantity())
                            .size(orderItem.getSize())
                            .unitPrice(orderItem.getUnitPrice())
                            .discountPrice(orderItem.getDiscountPrice())
                            .image(orderItem.getImage())
                            .build()
                    )
            );
            orderDTO.getOrderShipmentDTOList().add(orderShipmentDTO);
        });

        return orderDTO;
    }

    public List<Order> getOrdersByUserKeycloakId(String kcId) {
        return orderRepository.getOrderByUserKeycloakId(kcId);
    }
}
