package com.portshop.product_service.repository;

import com.portshop.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> getProductById(UUID id);
    List<Product> findAllByOrderByCreatedAtAsc();

    List<Product> getProductByShopKeycloakId(String shopKeycloakId);

    List<Product> findAllByOrderByCreatedAtDesc();
}
