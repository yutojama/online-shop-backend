package com.portshop.api_gateway.service;

import com.portshop.api_gateway.exception.AuthSessionNotFoundException;
import com.portshop.api_gateway.model.AuthSession;
import com.portshop.api_gateway.repository.AuthRepository;
import jakarta.persistence.Access;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthDbService {

    private final AuthRepository authRepository;
    private final KeycloakAuthService keycloakAuthService;

    public AuthDbService(AuthRepository authRepository, KeycloakAuthService keycloakAuthService) {
        this.authRepository = authRepository;
        this.keycloakAuthService = keycloakAuthService;
    }

    // return authSession id
    public String saveAuthSession(String sessionId, String keycloakId, AccessTokenResponse accessTokenResponse) {

        AuthSession authSession = authRepository.getAuthSessionByKeycloakUserId(keycloakId);
        if (authSession == null) {
            authSession = AuthSession.builder()
                    .sessionId(sessionId)
                    .keycloakUserId(keycloakId)
                    .accessToken(accessTokenResponse.getToken())
                    .refreshToken(accessTokenResponse.getRefreshToken())
                    .accessTokenExpiresAt(Instant.now().plusSeconds(accessTokenResponse.getExpiresIn()))
                    .refreshTokenExpiresAt(Instant.now().plusSeconds(accessTokenResponse.getRefreshExpiresIn()))
                    .build();
        } else {
            authSession.setSessionId(sessionId);
            authSession.setAccessToken(accessTokenResponse.getToken());
            authSession.setRefreshToken(accessTokenResponse.getRefreshToken());
            authSession.setAccessTokenExpiresAt(Instant.now().plusSeconds(accessTokenResponse.getExpiresIn()));
            authSession.setRefreshTokenExpiresAt(Instant.now().plusSeconds(accessTokenResponse.getRefreshExpiresIn()));
        }

        AuthSession savedAuthSession = authRepository.save(authSession);
        return savedAuthSession.getId().toString();
    }

    public String getAccessToken(String keycloakId) {
        AuthSession authSession = authRepository.getAuthSessionByKeycloakUserId(keycloakId);
        if (authSession == null) {
            throw new AuthSessionNotFoundException("Auth session not found with keycloak ID: "+keycloakId);
        }
        return authSession.getAccessToken();
    }

    public String getValidAccessToken(String sessionId) {
        AuthSession session = authRepository.getAuthSessionBySessionId(sessionId);
        Instant expiresAt = session.getAccessTokenExpiresAt();
        // check if access token has expired, then refresh
        if (expiresAt.isAfter(Instant.now().plusSeconds(20))) {
            return session.getAccessToken();
        }
        AccessTokenResponse accessTokenResponse = keycloakAuthService.refreshAccessToken(sessionId);
        // save the new jwt
        session.setAccessToken(accessTokenResponse.getToken());
        session.setRefreshToken(accessTokenResponse.getRefreshToken());
        session.setAccessTokenExpiresAt(Instant.now().plusSeconds(accessTokenResponse.getExpiresIn()));
        session.setRefreshTokenExpiresAt(Instant.now().plusSeconds(accessTokenResponse.getRefreshExpiresIn()));
        authRepository.save(session);
        return accessTokenResponse.getToken();
    }
}
