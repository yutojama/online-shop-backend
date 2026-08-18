package com.portshop.user_service.service;

import com.portshop.user_service.exception.ApiUserNotFoundException;
import com.portshop.user_service.userEnum.Role;
import com.portshop.user_service.repository.UserRepository;
import com.portshop.user_service.dto.UserRequestDTO;
import com.portshop.user_service.exception.KeycloakCreateUserException;
import com.portshop.user_service.model.ApiUser;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.representations.idm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserDbService userDbService;
    private final UserKeycloakService userKeycloakService;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    public UserService(UserDbService userDbService, UserKeycloakService userKeycloakService, UserDebugService userDebugService) {
        this.userDbService = userDbService;
        this.userKeycloakService = userKeycloakService;
    }

    public String createUser(UserRequestDTO userRequestDTO, Role role) {
        String kcId = userKeycloakService.createKeycloakUser(userRequestDTO);

        // try adding role
        try{
            userKeycloakService.updateUserRole(kcId, role);
        }catch (Exception e){
            userKeycloakService.deleteKeycloakUser(kcId);
        }

        // try creating db user
        String userId = null;
        try{
            userId = userDbService.createUser(kcId,role,userRequestDTO);
        }catch (Exception e){
            userKeycloakService.deleteKeycloakUser(kcId);
        }

        return userId;
    }

    public void updateUserRole(String userId, Role role) {
        Role preRole = userDbService.getRole(userId);
        String kcId = userDbService.updateUserRole(userId,role);

        // try updating role
        try{
            userKeycloakService.updateUserRole(kcId, role);
        }catch (Exception e){
            userDbService.updateUserRole(kcId,preRole);
        }
    }

    public void deleteUser(String userId) {
        String kcId = userDbService.deleteUser(userId);
        userKeycloakService.deleteKeycloakUser(kcId);
    }

}
