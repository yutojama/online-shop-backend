package com.portshop.order_service.repository;

import com.portshop.order_service.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    Cart findByUserKeycloakId(String userKeycloakId);
}
