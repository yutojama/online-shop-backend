package com.portshop.product_service.service;

import com.portshop.product_service.model.Item;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.productEnum.Category;
import com.portshop.product_service.productEnum.Gender;
import com.portshop.product_service.productEnum.Size;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ItemService itemService;

    /* Item test */
    @Test
    public void shouldAddItemWhileItemNotExists() {

        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = Product.builder()
                .id(productId)
                .shopId(UUID.randomUUID().toString())
                .brand("burton")
                .category(Category.top)
                .gender(Gender.M)
                .description("Water tight and wind proof")
                .quantity(10)
                .origPrice(200)
                .image("image")
                .build();

        Item item = Item.builder()
                .quantity(1)
                .size(Size.M)
                .build();
        item.setProduct(product);

        Item savedItem = Item.builder()
                .id(itemId)
                .quantity(1)
                .size(Size.M)
                .build();
        savedItem.setProduct(product);

        when(productService.updateProduct(product)).thenReturn(product);

        Item result = itemService.addItem(item);
        assertEquals(result.getSize(), savedItem.getSize());
        assertEquals(result.getQuantity(), savedItem.getQuantity());
    }

    @Test
    public void shouldAddItemWhileItemExists() {

        // GIVEN
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = Product.builder()
                .id(productId)
                .shopId(UUID.randomUUID().toString())
                .brand("burton")
                .category(Category.top)
                .gender(Gender.M)
                .description("Water tight and wind proof")
                .quantity(1)
                .origPrice(200)
                .image("image")
                .build();

        Item item = Item.builder()
                .quantity(1)
                .size(Size.M)
                .build();
        item.setProduct(product);

        product.getItems().add(item);

        Integer prodQuantityBefore = product.getQuantity();
        Integer itemQuantityBefore = item.getQuantity();
        System.out.println("Product quantity before: "+prodQuantityBefore);
        System.out.println("Item quantity before: "+itemQuantityBefore);


        // WHEN
        Item itemToBeAdded = Item.builder()
                .quantity(1)
                .size(Size.M)
                .build();
        itemToBeAdded.setProduct(product);

        when(productService.updateProduct(product)).thenReturn(product);
        Item result = itemService.addItem(itemToBeAdded);

        Integer prodQuantityAfter = product.getQuantity();
        Integer itemQuantityAfter = item.getQuantity();
        System.out.println("Product quantity after: "+prodQuantityAfter);
        System.out.println("Item quantity after: "+itemQuantityAfter);

        // THEN
        assertEquals(prodQuantityBefore+1, prodQuantityAfter);
        assertEquals(itemQuantityBefore+1, itemQuantityAfter);
    }

}
