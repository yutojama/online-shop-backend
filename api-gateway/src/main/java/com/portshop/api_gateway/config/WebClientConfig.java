package com.portshop.api_gateway.config;

import com.portshop.api_gateway.service.AuthDbService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient orderServiceWebClient(
            @Value("${app.order-service.url}") String orderServiceUrl,
            ExchangeFilterFunction bearerTokenFilter
    ) {
        return WebClient.builder()
                .baseUrl(orderServiceUrl)
                .filter(bearerTokenFilter)
                .build();
    }

    @Bean
    public WebClient keycloakWebClient() {
        return WebClient.builder().build();
    }

    @Bean
    public WebClient userServiceWebClient(
            @Value("${app.user-service.url}") String userServiceUrl,
            ExchangeFilterFunction bearerTokenFilter
    ) {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .filter(bearerTokenFilter)
                .build();
    }

    // fetch access token when request other services with WebClient
    @Bean
    public ExchangeFilterFunction getExchangeFilterFunction(AuthDbService authDbService) {
        return (request, next) ->

            ReactiveSecurityContextHolder.getContext()
                    .flatMap(securityContext -> {
                        Authentication authentication = securityContext.getAuthentication();
                        if (authentication == null || !authentication.isAuthenticated()) {return next.exchange(request);}

                        return Mono.deferContextual(contextView -> {
                            ServerWebExchange exchange = contextView.get(ServerWebExchange.class);

                            String sessionId = Optional.ofNullable(
                                    exchange.getRequest()
                                            .getCookies()
                                            .getFirst("SESSION")
                                    )
                                    .map(HttpCookie::getValue)
                                    .orElse(null);
                            return authDbService.getValidAccessToken(sessionId)
                                    .flatMap(accessToken -> {
                                        ClientRequest authenticationRequest =
                                                ClientRequest.from(request)
                                                        .headers(headers -> headers.setBearerAuth(accessToken)).build();

                                        return next.exchange(authenticationRequest);
                                    });
                        });
                    })
                    .switchIfEmpty(next.exchange(request));
    }

}
