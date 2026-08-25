package com.portshop.api_gateway.config;

import com.portshop.api_gateway.service.AuthDbService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;


/**
 * This filter gets jwt access token from auth db by cookie
 * add this as bearer token on request for downstream services
 * */
@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final AuthDbService authDbService;

    public JwtValidationGatewayFilterFactory(AuthDbService authDbService) {
        this.authDbService = authDbService;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return  (exchange, chain) -> {
            // get session ID
            ServerHttpRequest request = exchange.getRequest();
            String sessionId = request.getCookies().getFirst("SESSION").getValue();
            return authDbService.getValidAccessToken(sessionId)
                    .flatMap(accessToken -> {
                        ServerHttpRequest requestNew = request.mutate()
                                .header("Authorization", "Bearer " + accessToken)
                                .build();
                        return chain.filter(exchange.mutate().request(requestNew).build());
                    });
        };
    }
}
