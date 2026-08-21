package com.portshop.api_gateway.repository;

import com.portshop.api_gateway.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface authRepository extends JpaRepository<AuthToken, UUID> {
}
