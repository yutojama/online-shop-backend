package com.portshop.product_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userServiceWebClient(
            @Value("${app.user-service.url}") String userServiceUrl
    ) {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

}
