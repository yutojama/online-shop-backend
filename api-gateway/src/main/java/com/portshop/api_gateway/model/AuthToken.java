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
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String keycloakUserId;

    @Column(unique = true, nullable = false)
    private String accessToken;

    @Column(unique = true, nullable = false)
    private String refreshToken;

    @Column(unique = true, nullable = false)
    private Instant accessTokenExpiresAt;

}
