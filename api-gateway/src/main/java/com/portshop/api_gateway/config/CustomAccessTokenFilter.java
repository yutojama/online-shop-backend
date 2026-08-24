package com.portshop.api_gateway.config;

import com.portshop.api_gateway.service.AuthDbService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;


public class CustomAccessTokenFilter {

    public static HandlerFilterFunction<ServerResponse, ServerResponse> proxyAccessTokenFilter(AuthDbService authDbService) {
        return (request, next) -> {

            String sessionId = request.servletRequest().getSession().getId();

            String accessToken = authDbService.getValidAccessToken(sessionId);

            // Add token to the outgoing gateway request
            ServerRequest modifiedRequest = ServerRequest.from(request)
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + accessToken
                    )
                    .build();

            return next.handle(modifiedRequest);
        };
    }
}
