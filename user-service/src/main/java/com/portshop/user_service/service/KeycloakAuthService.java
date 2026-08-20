package com.portshop.user_service.service;

import com.portshop.user_service.dto.UserLoginRequestDTO;
import com.portshop.user_service.exception.ApiUserNotFoundException;
import com.portshop.user_service.exception.InvalidKeycloakCredentialsException;
import org.apache.http.auth.InvalidCredentialsException;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class KeycloakAuthService {

    private final WebClient webClient;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public KeycloakAuthService(WebClient webClient) {
        this.webClient = webClient;
    }

    public AccessTokenResponse getAccessToken(UserLoginRequestDTO userLoginRequestDTO) {
        try {
            return webClient.post()
                    .uri("http://localhost:8081/realms/portshop-realm/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("client_id",clientId)
                            .with("client_secret", clientSecret)
                            .with("grant_type", "password")
                            .with("username", userLoginRequestDTO.getUsername())
                            .with("password", userLoginRequestDTO.getPassword())
                    )
                    .retrieve()
                    .bodyToMono(AccessTokenResponse.class)
                    .block();
        } catch (WebClientResponseException.BadRequest e) {
            throw new InvalidKeycloakCredentialsException(e.getMessage());
        }
    }

    public void logout(String refreshToken) {
        webClient.post()
                .uri("http://localhost:8081/realms/portshop-realm/protocol/openid-connect/logout")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(
                        BodyInserters.fromFormData("client_id", clientId)
                                .with("client_secret", clientSecret)
                                .with("refresh_token", refreshToken)
                )
                .retrieve()
                .toBodilessEntity()
                .block();
    }

}
