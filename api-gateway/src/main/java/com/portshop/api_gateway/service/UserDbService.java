package com.portshop.api_gateway.service;

import com.portshop.api_gateway.dto.RegisterRequestDTO;
import com.portshop.api_gateway.dto.UserCreateRequestDTO;
import com.portshop.api_gateway.userEnum.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserDbService {

    private final WebClient webClient;

    public UserDbService(@Qualifier("userServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // return user id
    public Mono<String> createUser(String keycloakUserId, Role role, RegisterRequestDTO registerRequestDTO) {
        // create customer and save in db
        UserCreateRequestDTO userCreateRequestDTO = UserCreateRequestDTO.builder()
                .keycloakId(keycloakUserId)
                .role(role)
                .email(registerRequestDTO.getEmail())
                .firstName(registerRequestDTO.getFirstName())
                .lastName(registerRequestDTO.getLastName())
                .phone(registerRequestDTO.getPhone())
                .username(registerRequestDTO.resolveUsername())
                .build();

        return webClient.post()
                .uri("/api/users/create", userCreateRequestDTO)
                .retrieve()
                .bodyToMono(String.class);
    }

    // this is the admin version to test if the web client could add valid token on request
    public Mono<Object> getUser(String keycloakUserId) {
        return webClient.get()
                .uri("/api/users/get-user/{keycloakUserId}", keycloakUserId)
                .retrieve()
                .bodyToMono(Object.class);
    }

    // return keycloak id
    public Mono<String> updateUserRole(String userId, Role role) {
        return webClient.post()
                .uri("/api/users/{userId}/role/{role}", userId, role)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<Role> getRole(String userId) {
        return webClient.get()
                .uri("/api/users/get-role/{userId}", userId)
                .retrieve()
                .bodyToMono(Role.class);
    }

    // return keycloak id
    public Mono<String> deleteUser(String userId) {
        return webClient.delete()
                .uri("/api/users/delete-user/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<Void> deleteAllUsers() {
        return webClient.delete()
                .uri("/admin/users/reset-all")
                .retrieve()
                .bodyToMono(String.class).then();
    }
}
