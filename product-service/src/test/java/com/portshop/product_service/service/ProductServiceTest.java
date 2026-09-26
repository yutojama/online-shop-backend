package com.portshop.product_service.service;

import com.portshop.product_service.exception.ProductNotFoundException;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.productEnum.Category;
import com.portshop.product_service.productEnum.Gender;
import com.portshop.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    /* get product test*/
    @Test
    void shouldGetProductById(){

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

        when(productRepository.getProductById(productId)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(productId);
        assertEquals("Water tight and wind proof", result.getDescription());
        assertEquals("image", result.getImage());
        assertEquals(Gender.M, result.getGender());
        assertEquals("burton", result.getBrand());

//        verify(productRepository).findById(productId);

    }

    @Test
    void shouldThrowExceptionWhenProductNotFound(){
        UUID productId = UUID.randomUUID();

        when(productRepository.getProductById(productId)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(productId)
        );

        verify(productRepository).getProductById(productId);
    }

    /* delete product test */
    @Test
    void shouldDeleteProductById(){
        UUID productId = UUID.randomUUID();

        when(productRepository.getProductById(productId)).thenReturn(Optional.of(Product.builder().id(productId).build()));

        productService.deleteProductById(productId);

        verify(productRepository,times(1)).deleteById(productId);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundWhileDeleting(){
        UUID productId = UUID.randomUUID();

        when(productRepository.getProductById(productId)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(productId)
        );

    }

}
