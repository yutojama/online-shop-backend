package com.portshop.api_gateway.service;

import com.portshop.api_gateway.exception.AuthSessionNotFoundException;
import com.portshop.api_gateway.model.AuthSession;
import com.portshop.api_gateway.repository.AuthRepository;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthDbService {

    private final AuthRepository authRepository;

    public AuthDbService(AuthRepository authRepository) {
        this.authRepository = authRepository;
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

}
