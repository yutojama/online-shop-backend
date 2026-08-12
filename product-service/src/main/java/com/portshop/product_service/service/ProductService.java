package com.portshop.product_service.service;

import com.portshop.product_service.exception.ProductNotFoundException;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(UUID id) {
        Optional<Product> product = productRepository.getProductById(id);
        if(product.isPresent()) {
            return product.get();
        }
        throw new ProductNotFoundException("Product not found with ID: " + id);
    }

    public void deleteProductById(UUID id) {
        Optional<Product> product = productRepository.getProductById(id);
        if(product.isPresent()) {
            productRepository.deleteById(id);
        }else{
            throw new ProductNotFoundException("Product not found with ID: " + id);
        }

    }

    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

}
