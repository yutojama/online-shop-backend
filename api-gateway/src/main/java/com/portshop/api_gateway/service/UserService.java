package com.portshop.api_gateway.service;

import com.portshop.api_gateway.dto.RegisterRequestDTO;
import com.portshop.api_gateway.userEnum.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserDbService userDbService;
    private final KeycloakClientService keycloakClientService;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    public UserService(UserDbService userDbService, KeycloakClientService keycloakClientService) {
        this.userDbService = userDbService;
        this.keycloakClientService = keycloakClientService;
    }

    public Mono<String> createUser(RegisterRequestDTO registerRequestDTO, Role role) {
        Mono<String> kcIdMono = keycloakClientService.createKeycloakUser(registerRequestDTO);

        // try adding role
        Mono<Void> addRoleMono = kcIdMono.flatMap(kcId ->
                keycloakClientService.updateUserRole(kcId, role)
                        .onErrorResume(e -> keycloakClientService.deleteKeycloakUser(kcId).then(Mono.error(e)))
        );

        // try creating db user
        return addRoleMono.then(
                // this returns userId
                kcIdMono.flatMap(kcId ->
                    userDbService.createUser(kcId,role,registerRequestDTO)
                            .onErrorResume(e -> keycloakClientService.deleteKeycloakUser(kcId).then(Mono.error(e)))
                )
        );

    }

    public Mono<Void> updateUserRole(String userId, Role role) {
        Mono<Role> preRoleMono = userDbService.getRole(userId);
        Mono<String> kcIdMono = userDbService.updateUserRole(userId,role);

        // try updating role
        return kcIdMono.flatMap(kcId ->
                keycloakClientService.updateUserRole(kcId,role)
                        .onErrorResume(e-> preRoleMono.flatMap(preRole->
                                userDbService.updateUserRole(kcId,role)
                                ).then(Mono.error(e))
                        )
        ).then();
    }

    public Mono<Void> deleteUser(String userId) {
        Mono<String> kcIdMono = userDbService.deleteUser(userId);
        return kcIdMono.flatMap(keycloakClientService::deleteKeycloakUser);
    }

    public Mono<Void> deleteAllUsers() {
        return userDbService.deleteAllUsers()
                        .then(keycloakClientService.deleteAllKeycloakUsers());
    }

}
