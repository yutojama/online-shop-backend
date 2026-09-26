package com.portshop.order_service.service;

import com.portshop.order_service.dto.CartSumResponseDTO;
import com.portshop.order_service.exception.ItemNotFoundException;
import com.portshop.order_service.model.Cart;
import com.portshop.order_service.model.CartItem;
import com.portshop.order_service.orderEnum.Size;
import com.portshop.order_service.repository.CartRepository;
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
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    /* Test when no cart existing */
    @Test
    void shouldGetNoCart() {
        String kcId = UUID.randomUUID().toString();
        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .userKeycloakId(kcId)
                .build();

        when(cartRepository.findByUserKeycloakId(kcId)).thenReturn(null);

        assertThrows(
                ItemNotFoundException.class,
                () -> cartService.getCart(kcId)
        );

    }

    /* test when cart already exists */
    @Test
    void shouldGetCart() {
        String kcId = UUID.randomUUID().toString();
        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .userKeycloakId(kcId)
                .build();

        when(cartRepository.findByUserKeycloakId(kcId)).thenReturn(cart);
        assertEquals(cartService.getCart(kcId).getUserKeycloakId(), kcId);

    }

    @Test
    void shouldGetCartSum() {

        String kcId = UUID.randomUUID().toString();
        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .userKeycloakId(kcId)
                .build();

        CartItem cartItem1 = CartItem.builder()
                .id(UUID.randomUUID())
                .shopId(UUID.randomUUID().toString())
                .quantity(2)
                .size(Size.M)
                .itemId(UUID.randomUUID().toString())
                .productId(UUID.randomUUID().toString())
                .productName("Atomic Redster X9 Performance Skis With Bindings")
                .image("image1")
                .unitPrice(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(80))
                .daysOfDelivery(2)
                .available(5)
                .build();

        CartItem cartItem2 = CartItem.builder()
                .id(UUID.randomUUID())
                .shopId(UUID.randomUUID().toString())
                .quantity(1)
                .size(Size.S)
                .itemId(UUID.randomUUID().toString())
                .productId(UUID.randomUUID().toString())
                .productName("Fischer RC One All Mountain Skis")
                .image("image2")
                .unitPrice(BigDecimal.valueOf(120))
                .discountPrice(null)
                .daysOfDelivery(2)
                .available(8)
                .build();

        cartItem1.setCart(cart);
        cartItem2.setCart(cart);
        cart.addCartItem(cartItem1);
        cart.addCartItem(cartItem2);

        CartSumResponseDTO cartSumResponseDTO = cartService.getCartSum(cart);
        assertEquals(
                0,
                cartItem1.getUnitPrice()
                        .subtract(cartItem1.getDiscountPrice())
                        .multiply(BigDecimal.valueOf(cartItem1.getQuantity()))
                        .compareTo(cartSumResponseDTO.getDiscount())
        );
        assertEquals(
                0,
                cartSumResponseDTO.getShippingCost().compareTo(BigDecimal.ZERO)
        );
        assertEquals(
                0,
                cartSumResponseDTO.getTotal().compareTo(BigDecimal.valueOf(2*80+120))
        );

    }

    @Test
    void shouldAddItemToCartWhenItemNotExist() {

        String kcId = UUID.randomUUID().toString();
        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .userKeycloakId(kcId)
                .build();

        UUID cartItemId = UUID.randomUUID();
        CartItem cartItem = CartItem.builder()
                .id(cartItemId)
                .shopId(UUID.randomUUID().toString())
                .quantity(2)
                .size(Size.M)
                .itemId(UUID.randomUUID().toString())
                .productId(UUID.randomUUID().toString())
                .productName("Atomic Redster X9 Performance Skis With Bindings")
                .image("image1")
                .unitPrice(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(80))
                .daysOfDelivery(2)
                .available(5)
                .build();

        when(cartRepository.save(cart)).thenReturn(cart);

        Cart result = cartService.addItemToCart(cart, cartItem);

        assertEquals(result.getCartItems().getFirst().getId(), cartItem.getId());
        assertEquals(result.getCartItems().getFirst().getProductName(), cartItem.getProductName());

    }

    @Test
    void shouldAddItemToCartWhenItemExist() {

        String kcId = UUID.randomUUID().toString();
        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .userKeycloakId(kcId)
                .build();

        UUID cartItemId = UUID.randomUUID();
        String ItemId = UUID.randomUUID().toString();
        CartItem cartItem1 = CartItem.builder()
                .id(cartItemId)
                .shopId(UUID.randomUUID().toString())
                .quantity(2)
                .size(Size.M)
                .itemId(ItemId)
                .productId(UUID.randomUUID().toString())
                .productName("Atomic Redster X9 Performance Skis With Bindings")
                .image("image1")
                .unitPrice(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(80))
                .daysOfDelivery(2)
                .available(5)
                .build();

        CartItem cartItem2 = CartItem.builder()
                .id(cartItemId)
                .shopId(UUID.randomUUID().toString())
                .quantity(1)
                .size(Size.M)
                .itemId(ItemId)
                .productId(UUID.randomUUID().toString())
                .productName("Atomic Redster X9 Performance Skis With Bindings")
                .image("image1")
                .unitPrice(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(80))
                .daysOfDelivery(2)
                .available(5)
                .build();

        cart.getCartItems().add(cartItem1);
        cartItem1.setCart(cart);

        when(cartRepository.save(cart)).thenReturn(cart);

        Cart result = cartService.addItemToCart(cart, cartItem2);

        assertEquals(result.getCartItems().getFirst().getQuantity(), 3);
    }

}
