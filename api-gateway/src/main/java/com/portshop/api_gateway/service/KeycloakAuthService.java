package com.portshop.api_gateway.service;

import com.portshop.api_gateway.dto.LoginRequestDTO;
import com.portshop.api_gateway.exception.InvalidKeycloakCredentialsException;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class KeycloakAuthService {

    private final WebClient webClient;
    private final JwtDecoder jwtDecoder;

    @Value("${keycloak.token-url}")
    private String tokenUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public KeycloakAuthService(
            @Qualifier("keycloakWebClient") WebClient webClient,
            JwtDecoder jwtDecoder) {
        this.webClient = webClient;
        this.jwtDecoder = jwtDecoder;
    }

    public AccessTokenResponse getAccessToken(LoginRequestDTO loginRequestDTO) {
        try {
            return webClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("client_id",clientId)
                            .with("client_secret", clientSecret)
                            .with("grant_type", "password")
                            .with("username", loginRequestDTO.getUsername())
                            .with("password", loginRequestDTO.getPassword())
                    )
                    .retrieve()
                    .bodyToMono(AccessTokenResponse.class)
                    .block();
        } catch (WebClientResponseException.BadRequest e) {
            throw new InvalidKeycloakCredentialsException(e.getMessage());
        }
    }

    public String getKeycloakId(AccessTokenResponse accessTokenResponse) {
        Jwt jwt = jwtDecoder.decode(accessTokenResponse.getToken());
        return jwt.getSubject();
    }

    public String getKeycloakRole(AccessTokenResponse accessTokenResponse) {
        Jwt jwt = jwtDecoder.decode(accessTokenResponse.getToken());
        Map<String,Object> resourceAccess = jwt.getClaim("resource_access");
        Map<String,Object> portShopAdmin = (Map<String, Object>)resourceAccess.get("portshop-admin");
        List<String> roles = (List<String>)portShopAdmin.get("roles");
        return roles.getFirst();
    }

    public AccessTokenResponse refreshAccessToken(String refreshToken) {
        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("refresh_token", refreshToken))
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .block();
    }

    public void logout(String refreshToken) {
        webClient.post()
                .uri(tokenUrl)
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
