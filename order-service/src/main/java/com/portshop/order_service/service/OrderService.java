package com.portshop.order_service.service;

import com.portshop.order_service.dto.CartSumResponseDTO;
import com.portshop.order_service.model.*;
import com.portshop.order_service.orderEnum.Carrier;
import com.portshop.order_service.orderEnum.OrderStatus;
import com.portshop.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    public Order createOrderFromCart(Cart cart,
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
                .carrier(carrier)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .build();

        // add items from cart to order
        cartItems
                .forEach(cartItem ->
                    order.addOrderItem(
                            OrderItem.builder()
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
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(OrderStatus.PENDING)
                        .toStatus(OrderStatus.CONFIRMED)
                        .changedAt(Instant.now())
                        .changedBy("Shop")
                        .reason("Order created")
                        .build()
        );
        return orderRepository.save(order);
    }

    public Order processOrderHistory(Order order) {
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(OrderStatus.CONFIRMED)
                        .toStatus(OrderStatus.PROCESSING)
                        .changedAt(Instant.now())
                        .changedBy("Shop")
                        .reason("Order in process")
                        .build()
        );
        return orderRepository.save(order);
    }

    public Order shippingOrderHistory(Order order) {
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(OrderStatus.PROCESSING)
                        .toStatus(OrderStatus.SHIPPED)
                        .changedAt(Instant.now())
                        .changedBy("Carrier")
                        .reason("Order shipped")
                        .build()
        );
        return orderRepository.save(order);
    }

    public Order deliverOrderHistory(Order order) {
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(OrderStatus.SHIPPED)
                        .toStatus(OrderStatus.DELIVERED)
                        .changedAt(Instant.now())
                        .changedBy("Carrier")
                        .reason("Order delivered")
                        .build()
        );
        return orderRepository.save(order);
    }

    public Order cancelOrderHistory(Order order, OrderStatus fromStatus) {
        order.addOrderStatusHistory(
                OrderStatusHistory.builder()
                        .order(order)
                        .fromStatus(fromStatus)
                        .toStatus(OrderStatus.CANCELLED)
                        .changedAt(Instant.now())
                        .changedBy("Customer")
                        .reason("Order delivered")
                        .build()
        );
        return orderRepository.save(order);
    }

}
