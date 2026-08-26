package com.portshop.api_gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthSession {

    @Id
    private UUID id;

    private String sessionId;

    private String username;

    private String keycloakUserId;

    private String accessToken;

    private String refreshToken;

    private Instant accessTokenExpiresAt;

    private Instant refreshTokenExpiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
