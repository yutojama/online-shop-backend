package com.portshop.product_service.controller;

import com.portshop.product_service.dto.ItemMapper;
import com.portshop.product_service.dto.ProductMapper;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.productEnum.Category;
import com.portshop.product_service.productEnum.Gender;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PublicController.class,
        excludeAutoConfiguration = {
                OAuth2ResourceServerWebSecurityAutoConfiguration.class
        }
)
@Import({
        ProductMapper.class,
        ItemMapper.class
})
@AutoConfigureMockMvc(addFilters = false)
public class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private ItemService itemService;

    @Test
    void shouldGetProductById() throws Exception {

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

        when(productService.getProductById(productId)).thenReturn(product);

        mockMvc.perform(get("/public/products/get-prod/"+productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.brand").value("burton"))
                .andExpect(jsonPath("$.category").value(Category.top.toString()))
                .andExpect(jsonPath("$.gender").value(Gender.M.toString()))
                .andExpect(jsonPath("$.description").value("Water tight and wind proof"));


    }

}
