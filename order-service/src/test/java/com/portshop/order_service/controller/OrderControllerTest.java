package com.portshop.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portshop.order_service.dto.AddressRequestDTO;
import com.portshop.order_service.dto.OrderShippingInfoRequestDTO;
import com.portshop.order_service.dto.orderDTO.ItemReservationDTO;
import com.portshop.order_service.dto.orderDTO.OrderDTO;
import com.portshop.order_service.dto.orderDTO.OrderShipmentDTO;
import com.portshop.order_service.model.*;
import com.portshop.order_service.orderEnum.Carrier;
import com.portshop.order_service.orderEnum.PaymentMethod;
import com.portshop.order_service.orderEnum.Size;
import com.portshop.order_service.repository.CartRepository;
import com.portshop.order_service.repository.OrderItemRepository;
import com.portshop.order_service.repository.OrderRepository;
import com.portshop.order_service.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.web.OAuth2ResourceServerWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = OrderController.class,
        excludeAutoConfiguration = {
                OAuth2ResourceServerWebSecurityAutoConfiguration.class
        }
)
@Import({
        OrderService.class,
        CartService.class,
})
//@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /* mocks for order service */
    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @MockitoSpyBean
    private CartService cartService;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoSpyBean
    private OrderService orderService;

    /* mocks for order controller */
    @MockitoBean
    private KafkaOrderProducerService kafkaOrderProducerService;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoBean
    private ItemChangeRecordService itemChangeRecordService;

    // shared test data
    private Address address;
    private Cart cart;
    private Order order;
    private CartItem cartItem1;
    private CartItem cartItem2;
    private CartItem cartItem3;
    private OrderItem orderItem1;
    private OrderItem orderItem2;
    private OrderItem orderItem3;

    @BeforeEach
    void setUp() {

        String kcId = "lynn";

        cart = Cart.builder()
                .id(UUID.randomUUID())
                .userKeycloakId(kcId)
                .build();

        UUID orderId = UUID.randomUUID();
        order = Order.builder()
                .userKeycloakId(kcId)
                .id(orderId)
                .carrier(Carrier.DHL)
                .build();

        UUID shopId1 = UUID.randomUUID();
        UUID shopId2 = UUID.randomUUID();
        UUID itemId1 = UUID.randomUUID();
        UUID itemId2 = UUID.randomUUID();
        UUID itemId3 = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID productId3 = UUID.randomUUID();

        // create cart items
        cartItem1 = CartItem.builder()
                .id(UUID.randomUUID())
                .shopId(shopId1.toString())
                .quantity(1)
                .size(Size.S)
                .itemId(itemId1.toString())
                .productId(productId1.toString())
                .productName("Atomic Redster X9 Performance Skis With Bindings")
                .image("image1")
                .unitPrice(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(90))
                .daysOfDelivery(2)
                .available(10)
                .build();

        cartItem2 = CartItem.builder()
                .id(UUID.randomUUID())
                .shopId(shopId1.toString())
                .quantity(2)
                .size(Size.M)
                .itemId(itemId2.toString())
                .productId(productId2.toString())
                .productName("Fischer RC One All Mountain Skis")
                .image("image2")
                .unitPrice(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(180))
                .daysOfDelivery(2)
                .available(10)
                .build();

        cartItem3 = CartItem.builder()
                .id(UUID.randomUUID())
                .shopId(shopId2.toString())
                .quantity(3)
                .size(Size.L)
                .itemId(itemId3.toString())
                .productId(productId3.toString())
                .productName("Atomic Redster X9 Performance Skis With Bindings")
                .image("image1")
                .unitPrice(BigDecimal.valueOf(300))
                .discountPrice(null)
                .daysOfDelivery(2)
                .available(10)
                .build();

        // create order items
        UUID orderItemId1 = UUID.randomUUID();
        orderItem1 = OrderItem.builder()
                .id(orderItemId1)
                .shopId(shopId1.toString())
                .quantity(1)
                .size(Size.S)
                .itemId(itemId1.toString())
                .productId(productId1.toString())
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
                .itemId(itemId2.toString())
                .productId(productId2.toString())
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
                .itemId(itemId3.toString())
                .productId(productId3.toString())
                .productName("Oakley Mod5 Snow Helmet Matte Black")
                .image("image3")
                .unitPrice(BigDecimal.valueOf(300))
                .discountPrice(null)
                .build();

        address = Address.builder()
                .id(UUID.randomUUID())
                .city("Charisview")
                .country("New Zealand")
                .street("85819 Brock Port")
                .firstName("John")
                .lastName("Doe")
                .postalCode("12345")
                .build();

        order.setBillingAddress(address);
        order.setShippingAddress(address);

    }

    @Test
    @WithMockUser(
            username = "lynn",
            roles = {"CUSTOMER"}
    )
    void shouldGetOrderWhenOrderNotExists() throws Exception {

        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);
        order.addOrderItem(orderItem3);

        logger.info("Test get-order: when order does not exist");

        List<Order> orders = new ArrayList<>();

        doReturn(orders).when(orderService)
                .getOrdersByUserKeycloakId("lynn");

        mockMvc.perform(
                    get("/api/orders/get-order")
                )
            .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(
            username = "lynn",
            roles = {"CUSTOMER"}
    )
    void shouldGetOrder() throws Exception {

        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);
        order.addOrderItem(orderItem3);


        logger.info("Test get-order: when order exists");

        List<Order> orders = List.of(order);

        doReturn(orders).when(orderService)
                .getOrdersByUserKeycloakId("lynn");

        mockMvc.perform(
                        get("/api/orders/get-order")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userKeycloakId").value("lynn"));

    }

    @Test
    @WithMockUser(
            username = "lynn",
            roles = {"CUSTOMER"}
    )
    void shouldCreateOrderWhenCartNotExists() throws Exception {

        AddressRequestDTO addressRequestDTO = AddressRequestDTO.builder()
                .firstName(address.getFirstName())
                .lastName(address.getLastName())
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();

        OrderShippingInfoRequestDTO orderShippingInfoRequestDTO = OrderShippingInfoRequestDTO.builder()
                .paymentMethod(PaymentMethod.PAYPAL)
                .carrier(Carrier.DHL)
                .billingAddress(addressRequestDTO)
                .shippingAddress(addressRequestDTO)
                .build();

        doReturn(cart).when(cartService)
                .getCart("lynn");

        mockMvc.perform(
                        post("/api/orders/create")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderShippingInfoRequestDTO))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cart is empty"));

    }

    @Test
    @WithMockUser(
            username = "lynn",
            roles = {"CUSTOMER"}
    )
    void shouldCreateOrderWhenCartExists() throws Exception {

        AddressRequestDTO addressRequestDTO = AddressRequestDTO.builder()
                .firstName(address.getFirstName())
                .lastName(address.getLastName())
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();

        OrderShippingInfoRequestDTO orderShippingInfoRequestDTO = OrderShippingInfoRequestDTO.builder()
                .paymentMethod(PaymentMethod.PAYPAL)
                .carrier(Carrier.DHL)
                .billingAddress(addressRequestDTO)
                .shippingAddress(addressRequestDTO)
                .build();

        cart.addCartItem(cartItem1);
        cart.addCartItem(cartItem2);
        cart.addCartItem(cartItem3);
        cartItem1.setCart(cart);
        cartItem2.setCart(cart);
        cartItem3.setCart(cart);

        doReturn(cart).when(cartService)
                .getCart("lynn");

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        {
                            Order order = invocation.getArgument(0);
                            if(order.getId()==null) order.setId(UUID.randomUUID());
                            List<Shipment>  shipments = order.getShipments();
                            shipments.forEach(shipment -> {
                                if(shipment.getId()==null) shipment.setId(UUID.randomUUID());
                            });
                            return order;
                        }
                );

        mockMvc.perform(
                        post("/api/orders/create")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderShippingInfoRequestDTO))
                )
                .andExpect(status().isOk());

        assertTrue(cart.getCartItems().isEmpty());

    }

    @Test
    @WithMockUser(
            username = "lynn",
            roles = {"CUSTOMER"}
    )
    void shouldCreateOrderRejected() throws Exception {

        AddressRequestDTO addressRequestDTO = AddressRequestDTO.builder()
                .firstName(address.getFirstName())
                .lastName(address.getLastName())
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();

        OrderShippingInfoRequestDTO orderShippingInfoRequestDTO = OrderShippingInfoRequestDTO.builder()
                .paymentMethod(PaymentMethod.PAYPAL)
                .carrier(Carrier.DHL)
                .billingAddress(addressRequestDTO)
                .shippingAddress(addressRequestDTO)
                .build();

        cartItem3.setAvailable(0);

        cart.addCartItem(cartItem1);
        cart.addCartItem(cartItem2);
        cart.addCartItem(cartItem3);
        cartItem1.setCart(cart);
        cartItem2.setCart(cart);
        cartItem3.setCart(cart);

        doReturn(cart).when(cartService)
                .getCart("lynn");

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        {
                            Order order = invocation.getArgument(0);
                            if(order.getId()==null) order.setId(UUID.randomUUID());
                            List<Shipment>  shipments = order.getShipments();
                            shipments.forEach(shipment -> {
                                if(shipment.getId()==null) shipment.setId(UUID.randomUUID());
                            });
                            return order;
                        }
                );

        when(inventoryService.reserveItem(any(OrderDTO.class)))
                .thenAnswer(invocation ->{
                    OrderDTO orderDTO = invocation.getArgument(0);
                    List<OrderShipmentDTO> orderShipmentDTOList = orderDTO.getOrderShipmentDTOList();
                    List<ItemReservationDTO> itemReservationDTOList = new ArrayList<>();
                    orderShipmentDTOList.forEach(orderShipmentDTO ->
                        orderShipmentDTO.getOrderItemDTOList().forEach(orderItemDTO ->
                                itemReservationDTOList.add(
                                        ItemReservationDTO.builder()
                                                .orderId(orderDTO.getOrderId())
                                                .itemId(orderItemDTO.getItemId())
                                                .quantity(orderItemDTO.getQuantity())
                                                .availability(
                                                        cart.getCartItems().stream()
                                                                .filter(cartItem -> cartItem.getItemId()==orderItemDTO.getItemId())
                                                                .findFirst()
                                                                .orElse(null).getAvailable()
                                                )
                                        .build()
                                )
                        )
                    );

                    // print all reservation status
                    itemReservationDTOList.forEach(itemReservationDTO ->
                            System.out.println("item " +itemReservationDTO.getItemId()+ " -- quantity: "+itemReservationDTO.getQuantity()+" available: "+itemReservationDTO.getAvailability())
                    );

                    // set message to rejected when quantity > availability
                    itemReservationDTOList.forEach(ir ->
                                ir.setMessage(
                                        ir.getQuantity()>ir.getAvailability()?"REJECTED":"RESERVED"
                                )
                            );
                    return itemReservationDTOList;
                });

        mockMvc.perform(
                        post("/api/orders/create")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderShippingInfoRequestDTO))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[2].message").value("REJECTED"));

    }

}
