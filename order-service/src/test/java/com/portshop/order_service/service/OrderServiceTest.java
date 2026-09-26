package com.portshop.order_service.service;

import com.portshop.order_service.exception.OrderNotFoundException;
import com.portshop.order_service.model.Order;
import com.portshop.order_service.model.OrderItem;
import com.portshop.order_service.orderEnum.Carrier;
import com.portshop.order_service.orderEnum.Size;
import com.portshop.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    // shared test data
    private Order order;
    private OrderItem orderItem1;
    private OrderItem orderItem2;
    private OrderItem orderItem3;

    @BeforeEach
    void setUp() {

        UUID orderId = UUID.randomUUID();
        order = Order.builder()
                .id(orderId)
                .carrier(Carrier.DHL)
                .build();

        UUID shopId1 = UUID.randomUUID();
        UUID shopId2 = UUID.randomUUID();

        UUID orderItemId1 = UUID.randomUUID();
        orderItem1 = OrderItem.builder()
                .id(orderItemId1)
                .shopId(shopId1.toString())
                .quantity(1)
                .size(Size.S)
                .itemId(UUID.randomUUID().toString())
                .productId(UUID.randomUUID().toString())
                .productName("Atomic Redster X9 Performance Skis With Bindings")
                .image("image1")
                .unitPrice(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(90))
                .build();

        UUID orderItemId2 = UUID.randomUUID();
        orderItem2 = OrderItem.builder()
                .id(orderItemId2)
                .shopId(shopId1.toString())
                .quantity(2)
                .size(Size.M)
                .itemId(UUID.randomUUID().toString())
                .productId(UUID.randomUUID().toString())
                .productName("Fischer RC One All Mountain Skis")
                .image("image2")
                .unitPrice(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(180))
                .build();

        UUID orderItemId3 = UUID.randomUUID();
        orderItem3 = OrderItem.builder()
                .id(orderItemId3)
                .shopId(shopId2.toString())
                .quantity(3)
                .size(Size.L)
                .itemId(UUID.randomUUID().toString())
                .productId(UUID.randomUUID().toString())
                .productName("Oakley Mod5 Snow Helmet Matte Black")
                .image("image3")
                .unitPrice(BigDecimal.valueOf(300))
                .discountPrice(null)
                .build();
    }


    @Test
    void shouldGetOrder() {

        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .build();

        when(orderRepository.getOrderById(orderId)).thenReturn(order);

        Order result = orderService.getOrder(orderId.toString());
        assertEquals(result.getId(),orderId);

    }

    @Test
    void shouldThrowWhenGetOrderFails() {

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrder(UUID.randomUUID().toString())
        );

    }

    @Test
    void shouldCreateOrderShipment() {

        order.getOrderItems().add(orderItem1);
        order.getOrderItems().add(orderItem2);
        order.getOrderItems().add(orderItem3);

        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.createOrderShipment(order);

        assertEquals(result.getShipments().stream().count(),2);
        assertEquals(result.getOrderItems().get(0).getShipment().getId(),result.getOrderItems().get(1).getShipment().getId());
    }

}
