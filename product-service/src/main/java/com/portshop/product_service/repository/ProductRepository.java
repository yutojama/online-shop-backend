package com.portshop.product_service.repository;

import com.portshop.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Optional<Product> getProductById(UUID id);
    List<Product> findAllByOrderByCreatedAtAsc();

    List<Product> getProductByShopId(String shopKeycloakId);

    List<Product> findAllByOrderByCreatedAtDesc();
}
