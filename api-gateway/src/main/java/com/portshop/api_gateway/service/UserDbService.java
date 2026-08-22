package com.portshop.api_gateway.service;

import com.portshop.api_gateway.dto.RegisterRequestDTO;
import com.portshop.api_gateway.dto.UserCreateRequestDTO;
import com.portshop.api_gateway.userEnum.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserDbService {

    private final WebClient webClient;

    public UserDbService(@Qualifier("userServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // return user id
    public String createUser(String keycloakUserId, Role role, RegisterRequestDTO registerRequestDTO) {
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
                .uri("/users/create", userCreateRequestDTO)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // return keycloak id
    public String updateUserRole(String userId, Role role) {
        return webClient.post()
                .uri("users/{userId}/role/{role}", userId, role)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public Role getRole(String userId) {
        return webClient.get()
                .uri("/users/get-role/{userId}", userId)
                .retrieve()
                .bodyToMono(Role.class)
                .block();
    }

    // return keycloak id
    public String deleteUser(String userId) {
        return webClient.delete()
                .uri("/delete-user/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public void deleteAllUsers() {

    }
}
