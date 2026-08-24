package com.portshop.user_service.repository;

import com.portshop.user_service.model.ApiUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<ApiUser, UUID> {
    Optional<ApiUser> getApiUserById(UUID id);
    Optional<ApiUser> getApiUserByKeycloakUserId(String keycloakUserId);
}
