package com.portshop.product_service.service;

import com.portshop.product_service.controller.ProductController;
import com.portshop.product_service.exception.ProductNotFoundException;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.productEnum.Category;
import com.portshop.product_service.productEnum.Gender;
import com.portshop.product_service.productEnum.Size;
import com.portshop.product_service.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<Product> getProductsByShopId(String shopId) {
        return productRepository.getProductByShopId(shopId);
    }

    public List<Product> findAllByOrderByCreatedAtAsc() {
        return productRepository.findAllByOrderByCreatedAtAsc();
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findAllByOrderByCreatedAtDesc() {
        return productRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Product> getFilteredProducts(Gender gender, Category cat, Size size) {
        Specification<Product> spec = filterProducts(gender,cat,size);
        return productRepository.findAll(spec);
    }

    public Specification<Product> filterProducts(Gender gender, Category cat, Size size) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (gender != null) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), gender));
            }
            if (cat != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), cat));
            }
            if (size != null) {
                predicates.add(criteriaBuilder.equal(root.get("size"), size));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }


    /* update product */
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public void updateAll(List<Product> products) {
        productRepository.saveAll(products);
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

    public Boolean verifyProduct(UUID userId, String kcId) {
        Optional<Product> product = productRepository.getProductById(userId);
        if(product.isPresent()) {
            return product.get().getShopId().equals(kcId);
        }
        throw new ProductNotFoundException("Product not found with ID: " + userId);
    }

    public void deleteAllProducts() {
        productRepository.deleteAll();
    }


}
