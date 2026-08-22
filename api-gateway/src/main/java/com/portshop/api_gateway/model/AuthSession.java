package com.portshop.api_gateway.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
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

//    @Column(unique = true, nullable = false)
//    private UUID userId;

    @Column(unique = true, nullable = false)
    private String keycloakUserId;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Instant accessTokenExpiresAt;

    private Instant refreshTokenExpiresAt;

}
