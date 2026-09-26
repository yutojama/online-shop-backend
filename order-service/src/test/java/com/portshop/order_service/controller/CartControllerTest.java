package com.portshop.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portshop.order_service.model.Cart;
import com.portshop.order_service.model.CartItem;
import com.portshop.order_service.orderEnum.Size;
import com.portshop.order_service.repository.CartRepository;
import com.portshop.order_service.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.web.OAuth2ResourceServerWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CartController.class,
        excludeAutoConfiguration = {
                OAuth2ResourceServerWebSecurityAutoConfiguration.class
        }
)
@Import({
        CartService.class,
})
//@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoSpyBean
    private CartService cartService;

    @MockitoBean
    private CartRepository cartRepository;

    // shared test data
    private Cart cart;
    private CartItem cartItem1;
    private CartItem cartItem2;
    private CartItem cartItem3;

    @BeforeEach
    void setUp() {

        String kcId = "lynn";

        cart = Cart.builder()
                .id(UUID.randomUUID())
                .userKeycloakId(kcId)
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

    }

    @Test
    @WithMockUser(
            username = "lynn",
            roles = {"CUSTOMER"}
    )
    void shouldGetOrderWhenCartIsEmpty() throws Exception {

        Cart cart = Cart.builder().build();

        doReturn(cart).when(cartService)
                .createCart("lynn");

        mockMvc.perform(
                        get("/api/carts/get-cart")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cart is empty"));

    }

    @Test
    @WithMockUser(
            username = "lynn",
            roles = {"CUSTOMER"}
    )
    void shouldGetOrderWhenCartExists() throws Exception {

        cart.addCartItem(cartItem1);
        cart.addCartItem(cartItem2);
        cart.addCartItem(cartItem3);

        doReturn(cart).when(cartService)
                .createCart("lynn");

        mockMvc.perform(
                        get("/api/carts/get-cart")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(""));

    }

}
