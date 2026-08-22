package com.portshop.api_gateway.service;

import com.portshop.api_gateway.dto.RegisterRequestDTO;
import com.portshop.api_gateway.userEnum.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public String createUser(RegisterRequestDTO registerRequestDTO, Role role) {
        String kcId = keycloakClientService.createKeycloakUser(registerRequestDTO);

        // try adding role
        try{
            keycloakClientService.updateUserRole(kcId, role);
        }catch (Exception e){
            keycloakClientService.deleteKeycloakUser(kcId);
        }

        // try creating db user
        String userId = null;
        try{
            userId = userDbService.createUser(kcId,role, registerRequestDTO);
        }catch (Exception e){
            keycloakClientService.deleteKeycloakUser(kcId);
        }

        return userId;
    }

    public void updateUserRole(String userId, Role role) {
        Role preRole = userDbService.getRole(userId);
        String kcId = userDbService.updateUserRole(userId,role);

        // try updating role
        try{
            keycloakClientService.updateUserRole(kcId, role);
        }catch (Exception e){
            userDbService.updateUserRole(kcId,preRole);
        }
    }

    public void deleteUser(String userId) {
        String kcId = userDbService.deleteUser(userId);
        keycloakClientService.deleteKeycloakUser(kcId);
    }

    public void deleteAllUsers() {
        userDbService.deleteAllUsers();
        keycloakClientService.deleteAllKeycloakUsers();
    }

}
