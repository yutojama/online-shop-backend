package com.portshop.api_gateway.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String sessionId;

    @Column(unique = true, nullable = false)
    private String keycloakUserId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false)
    private Instant accessTokenExpiresAt;

    @Column(nullable = false)
    private Instant refreshTokenExpiresAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
