package com.portshop.order_service.service;

import com.portshop.order_service.dto.CartSumResponseDTO;
import com.portshop.order_service.dto.ItemShortResponseDTO;
import com.portshop.order_service.exception.ItemNotFoundException;
import com.portshop.order_service.model.Cart;
import com.portshop.order_service.model.CartItem;
import com.portshop.order_service.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }


    /**
     * cart handling from here
     * */

    public Cart createCart(String kcId) {
        Cart cart = cartRepository.findByUserKeycloakId(kcId);
        if (cart == null) {
            cart = Cart.builder()
                    .userKeycloakId(kcId)
                    .build();
            return cartRepository.save(cart);
        }
        return cart;
    }

    public Cart getCart(String kcId) {
        Cart cart = cartRepository.findByUserKeycloakId(kcId);
        if (cart == null) {
            throw new ItemNotFoundException("Cart not found, admin should check database");
        }
        return cart;
    }

    public CartSumResponseDTO getCartSum(Cart cart) {

        // calculate subtotal
        BigDecimal subTotal = cart.getCartItems().stream()
                .map(cartItem -> cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // calculate total if all discounted price
        BigDecimal total = cart.getCartItems().stream()
                .map(cartItem -> {
                    Integer quantity = cartItem.getQuantity();
                    if(cartItem.getDiscountPrice() == null){
                        return cartItem.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
                    }
                    return cartItem.getDiscountPrice().multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // calculate discount
        BigDecimal discount = subTotal.subtract(total);
        BigDecimal tax = total.multiply(BigDecimal.valueOf(0.02));

        // define default tax and shipping cost
        BigDecimal leastTaxFreeTotal = BigDecimal.valueOf(50);
        BigDecimal shippingCostStandard = BigDecimal.valueOf(8);

        // calculate shipping cost
        BigDecimal shippingCost = null;
        if(cart.getCartItems().isEmpty()){
            shippingCost = BigDecimal.ZERO;
        } else {
            shippingCost = (total.compareTo(leastTaxFreeTotal) < 0)?
                    shippingCostStandard:BigDecimal.valueOf(0);
        }

        // calculate total
        BigDecimal endTotal = total.add(tax).add(shippingCost);

        // get item list
        List<ItemShortResponseDTO> itemList = cart.getCartItems().stream()
                .map(cartItem ->
                        ItemShortResponseDTO.builder()
                                .productName(cartItem.getProductName())
                                .quantity(cartItem.getQuantity())
                                .build()
                ).toList();


        return CartSumResponseDTO.builder()
                .itemList(itemList)
                .subTotal(subTotal.setScale(2, RoundingMode.HALF_UP))
                .total(endTotal.setScale(2, RoundingMode.HALF_UP))
                .discount(discount.setScale(2, RoundingMode.HALF_UP))
                .tax(tax.setScale(2, RoundingMode.HALF_UP))
                .shippingCost(shippingCost.setScale(2, RoundingMode.HALF_UP))
                .build();
    }


    /**
     *  cartItem handling from here
     *  */

    public CartItem getCartItemById(Cart cart, String itemId) {
        List<CartItem> cartItems = cart.getCartItems();
        CartItem itemFound = cartItems.stream()
                .filter(item -> item.getId().toString().equals(itemId))
                .findFirst().orElse(null);
        if (itemFound == null) {
            throw new ItemNotFoundException("Item not found in your cart");
        }
        return itemFound;
    }

    public Cart addItemToCart(Cart cart, CartItem cartItem) {
        List<CartItem> cartItems = cart.getCartItems();
        CartItem existItem = cartItems.stream().filter(item -> item.getItemId().equals(cartItem.getItemId()))
                .findFirst()
                .orElse(null);

        if (existItem == null) {
            cartItem.setCart(cart);
            cart.addCartItem(cartItem);
            return cartRepository.save(cart);
        }
        existItem.setQuantity(existItem.getQuantity() + cartItem.getQuantity());
        return cartRepository.save(cart);
    }

    public Cart removeItemFromCart(Cart cart, String itemId) {
        CartItem cartItem = getCartItemById(cart, itemId);
        cart.removeCartItem(cartItem);
        return cartRepository.save(cart);
    }

    public Cart plusItemToCart(Cart cart, String itemId) {
        CartItem cartItem = getCartItemById(cart, itemId);
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        return cartRepository.save(cart);
    }

    public Cart minusItemToCart(Cart cart, String itemId) {
        CartItem cartItem = getCartItemById(cart, itemId);
        Integer quantity = cartItem.getQuantity();
        if (quantity > 1) {
            cartItem.setQuantity(quantity - 1);
            return cartRepository.save(cart);
        } else {
            return removeItemFromCart(cart, itemId);
        }
    }

    public void clearCart(Cart cart) {
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems != null) {
            cartItems.clear();
            cartRepository.save(cart);
        }
    }

}
