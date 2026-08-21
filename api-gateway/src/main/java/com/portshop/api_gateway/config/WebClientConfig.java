package com.portshop.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${keycloak.token-url}")
    private String tokenUrl;

    @Value("${app.user-service.url}")
    private String userServiceUrl;

    @Bean
    public WebClient keycloakWebClient() {
        return WebClient.builder().build();
    }

    @Bean
    public WebClient userServiceWebClient() {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

}
