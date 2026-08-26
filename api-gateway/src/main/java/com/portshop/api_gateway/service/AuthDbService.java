package com.portshop.api_gateway.service;

import com.portshop.api_gateway.exception.AuthSessionNotFoundException;
import com.portshop.api_gateway.model.AuthSession;
import com.portshop.api_gateway.repository.AuthRepository;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class AuthDbService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthRepository authRepository;
    private final KeycloakAuthService keycloakAuthService;

    public AuthDbService(AuthRepository authRepository, KeycloakAuthService keycloakAuthService) {
        this.authRepository = authRepository;
        this.keycloakAuthService = keycloakAuthService;
    }

    // return authSession id
    public Mono<String> saveAuthSession(String sessionId, String username, AccessTokenResponse accessTokenResponse) {

        String keycloakId = keycloakAuthService.getKeycloakId(accessTokenResponse);

        return authRepository.getAuthSessionByUsername(username)
                .flatMap(authSession -> {
                    authSession.setSessionId(sessionId);
                    authSession.setKeycloakUserId(keycloakId);
                    authSession.setAccessToken(accessTokenResponse.getToken());
                    authSession.setRefreshToken(accessTokenResponse.getRefreshToken());
                    authSession.setAccessTokenExpiresAt(Instant.now().plusSeconds(accessTokenResponse.getExpiresIn()));
                    authSession.setRefreshTokenExpiresAt(Instant.now().plusSeconds(accessTokenResponse.getRefreshExpiresIn()));
                    return authRepository.save(authSession)
                            .map(AuthSession::getSessionId);
                })
                .switchIfEmpty(Mono.defer(()->{
                    AuthSession authSession = AuthSession.builder()
                            .sessionId(sessionId)
                            .username(username)
                            .keycloakUserId(keycloakId)
                            .accessToken(accessTokenResponse.getToken())
                            .refreshToken(accessTokenResponse.getRefreshToken())
                            .accessTokenExpiresAt(Instant.now().plusSeconds(accessTokenResponse.getExpiresIn()))
                            .refreshTokenExpiresAt(Instant.now().plusSeconds(accessTokenResponse.getRefreshExpiresIn()))
                            .build();
                    return authRepository.save(authSession)
                            .map(AuthSession::getSessionId);
                }));
    }

    public Mono<String> getAccessToken(String keycloakId) {
        return authRepository.getAuthSessionByKeycloakUserId(keycloakId)
                .switchIfEmpty(Mono.error(
                        new AuthSessionNotFoundException("Auth session not found with keycloak ID: "+keycloakId)
                ))
                .map(AuthSession::getAccessToken);
    }

    public Mono<String> getValidAccessToken(String sessionId) {
        return authRepository.getAuthSessionBySessionId(sessionId)
                .switchIfEmpty(Mono.error(
                        new AuthSessionNotFoundException("Auth session not found with sessionId: "+sessionId)
                ))
                .flatMap(this::getValidOrRefreshToken);
    }

    // logic part of getValidAccessToken
    private Mono<String> getValidOrRefreshToken(AuthSession authSession) {
        Instant expiresAt = authSession.getAccessTokenExpiresAt();
        // check if access token has expired, then refresh
        if (expiresAt.isAfter(Instant.now().plusSeconds(20))) {
            return Mono.just(authSession.getAccessToken());
        }
        return refreshToken(authSession);
    }

    private Mono<String> refreshToken(AuthSession authSession) {
        return keycloakAuthService.refreshAccessToken(authSession.getRefreshToken())
                .flatMap(response->{
                    logger.info("Access token refreshed");
                    // save the new jwt
                    authSession.setAccessToken(response.getToken());
                    authSession.setRefreshToken(response.getRefreshToken());
                    authSession.setAccessTokenExpiresAt(Instant.now().plusSeconds(response.getExpiresIn()));
                    authSession.setRefreshTokenExpiresAt(Instant.now().plusSeconds(response.getRefreshExpiresIn()));

                    return authRepository.save(authSession)
                            .thenReturn(response.getToken());
                });
    }

}
