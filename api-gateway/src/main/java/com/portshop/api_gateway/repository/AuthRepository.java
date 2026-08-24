package com.portshop.api_gateway.repository;

import com.portshop.api_gateway.model.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<AuthSession, UUID> {
    AuthSession getAuthSessionByKeycloakUserId(String keycloakUserId);

    AuthSession getAuthSessionBySessionId(String sessionId);
}
