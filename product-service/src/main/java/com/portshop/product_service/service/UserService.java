package com.portshop.product_service.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService {

    private final WebClient webClient;
    private final HttpServletRequest httpServletRequest;

    public UserService(@Qualifier("userServiceWebClient") WebClient webClient, HttpServletRequest httpServletRequest) {
        this.webClient = webClient;
        this.httpServletRequest = httpServletRequest;
    }

    public List<String> getUserIdByRole(String role){
        return webClient.get()
                .uri("/api/users/get-user/by-role/"+role)
                .header(HttpHeaders.AUTHORIZATION, httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }

}
