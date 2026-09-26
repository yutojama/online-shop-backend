package com.portshop.product_service.controller;

import com.portshop.product_service.dto.ItemMapper;
import com.portshop.product_service.model.Item;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.productEnum.Category;
import com.portshop.product_service.productEnum.Gender;
import com.portshop.product_service.productEnum.Size;
import com.portshop.product_service.service.InventoryService;
import com.portshop.product_service.service.ItemService;
import com.portshop.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.web.OAuth2ResourceServerWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ItemController.class,
        excludeAutoConfiguration = {
                OAuth2ResourceServerWebSecurityAutoConfiguration.class
        }
)
@Import(ItemMapper.class)
@AutoConfigureMockMvc(addFilters = false)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private InventoryService inventoryService;

    @Test
    void shouldPatchAddItemByShop() throws Exception {

        int itemQuantity = 1;
        UUID itemId = UUID.randomUUID();
        Item item = Item.builder()
                .id(itemId)
                .quantity(itemQuantity)
                .size(Size.M)
                .build();

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

        product.getItems().add(item);
        item.setProduct(product);

        when(itemService.getItem(itemId)).thenReturn(item);

        mockMvc.perform(patch("/api/items/shop/add/"+itemId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId.toString()))
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.quantity").value(itemQuantity+1));

    }

    @Test
    void shouldRemoveItemByShop() throws Exception {

        UUID itemId = UUID.randomUUID();
        Item item = Item.builder()
                .id(itemId)
                .quantity(1)
                .size(Size.M)
                .build();

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

        product.getItems().add(item);
        item.setProduct(product);

        when(itemService.getItem(itemId)).thenReturn(item);

        // when item quantity == 1, item quantity should be decreased to 0
        mockMvc.perform(patch("/api/items/shop/remove/"+itemId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId.toString()))
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.quantity").value(0));

        // when item quantity == 0, item quantity should no more decrease
        mockMvc.perform(patch("/api/items/shop/remove/"+itemId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId.toString()))
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.quantity").value(0));
    }

}
