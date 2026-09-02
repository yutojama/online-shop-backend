package com.portshop.order_service.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient inventoryServiceWebClient(
            @Value("${app.product-service.url}") String productServiceUrl
    ) {
        return WebClient.builder()

                // get access token from context and put it in webclient

                .filter((request, next) -> {

                    ServletRequestAttributes attributes =
                            (ServletRequestAttributes)
                                    RequestContextHolder.getRequestAttributes();

                    if (attributes == null) {
                        return next.exchange(request);
                    }

                    HttpServletRequest incomingRequest =
                            attributes.getRequest();

                    String authorization =
                            incomingRequest.getHeader(HttpHeaders.AUTHORIZATION);

                    if (authorization == null) {
                        return next.exchange(request);
                    }

                    ClientRequest modifiedRequest =
                            ClientRequest.from(request)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            authorization
                                    )
                                    .build();

                    return next.exchange(modifiedRequest);
                })
                .baseUrl(productServiceUrl)
                .build();
    }

    @Bean
    public WebClient userServiceWebClient(
            @Value("${app.user-service.url}") String userServiceUrl
    ) {
        return WebClient.builder()

                // get access token from context and put it in webclient

                .filter((request, next) -> {

                    ServletRequestAttributes attributes =
                            (ServletRequestAttributes)
                                    RequestContextHolder.getRequestAttributes();

                    if (attributes == null) {
                        return next.exchange(request);
                    }

                    HttpServletRequest incomingRequest =
                            attributes.getRequest();

                    String authorization =
                            incomingRequest.getHeader(HttpHeaders.AUTHORIZATION);

                    if (authorization == null) {
                        return next.exchange(request);
                    }

                    ClientRequest modifiedRequest =
                            ClientRequest.from(request)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            authorization
                                    )
                                    .build();

                    return next.exchange(modifiedRequest);
                })
                .baseUrl(userServiceUrl)
                .build();
    }

}
