package com.portshop.api_gateway.repository;

import com.portshop.api_gateway.model.AuthSession;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AuthRepository extends ReactiveCrudRepository<AuthSession, UUID> {
    Mono<AuthSession> getAuthSessionByKeycloakUserId(String keycloakUserId);

    Mono<AuthSession> getAuthSessionBySessionId(String sessionId);
}
