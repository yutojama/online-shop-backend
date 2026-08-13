package com.portshop.product_service.service;

import com.portshop.product_service.controller.ProductController;
import com.portshop.product_service.exception.ProductNotFoundException;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /* create product*/
    public Product createProduct(Product product) {
        Product newProduct = productRepository.save(product);
        logger.debug("REST request to save Product : {}", newProduct);
        return newProduct;
    }

    /* get product */
    public Product getProductById(UUID id) {
        Optional<Product> product = productRepository.getProductById(id);
        if(product.isPresent()) {
            return product.get();
        }
        throw new ProductNotFoundException("Product not found with ID: " + id);
    }

    public List<Product> findAllByOrderByCreatedAtAsc() {
        return productRepository.findAllByOrderByCreatedAtAsc();
    }


    /* update product */
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    /* delete product */
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
