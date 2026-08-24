package com.portshop.api_gateway.config;

import com.portshop.api_gateway.service.AuthDbService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

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

    // client filter
    @Bean
    public ExchangeFilterFunction getExchangeFilterFunction(AuthDbService authDbService) {
        return (request, next) -> {

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes)
                            RequestContextHolder.getRequestAttributes();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (attributes == null || !authentication.isAuthenticated()) {
                return next.exchange(request);
            }

            HttpServletRequest servletRequest =
                    attributes.getRequest();

            HttpSession session =
                    servletRequest.getSession(false);

            if (session == null) {
                return next.exchange(request);
            }

            String sessionId = session.getId();

            String accessToken =
                    authDbService.getValidAccessToken(sessionId);

            ClientRequest authenticatedRequest =
                    ClientRequest.from(request)
                            .headers(headers ->
                                    headers.setBearerAuth(accessToken))
                            .build();

            return next.exchange(authenticatedRequest);
        };
    }

}
