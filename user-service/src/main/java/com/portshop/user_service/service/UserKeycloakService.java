package com.portshop.user_service.service;

import com.portshop.user_service.dto.UserRequestDTO;
import com.portshop.user_service.exception.KeycloakCreateUserException;
import com.portshop.user_service.exception.KeycloakDeleteFailedException;
import com.portshop.user_service.userEnum.Role;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserKeycloakService {

    private final Logger logger = LoggerFactory.getLogger(UserKeycloakService.class);
    private final Keycloak kc;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    public UserKeycloakService(Keycloak kc) {
        this.kc = kc;
    }

    // return keycloak id
    public String createKeycloakUser(UserRequestDTO userRequestDTO) {
        // create keycloak user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userRequestDTO.resolveUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setEnabled(true);
        user.setEmailVerified(false);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userRequestDTO.getPassword());
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));

        String location;
        try (Response response = kc
                .realm(realm)
                .users()
                .create(user)) {

            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new KeycloakCreateUserException(String.valueOf(response.getStatus()));
            }

            // Location header contains the newly created user's URL
            location = response.getLocation().toString();
        }

        return location.substring(location.lastIndexOf("/") + 1);
    }

    public void updateUserRole(String keycloakUserId, Role role) {
        // get client id
        ClientRepresentation client = kc
                .realm(realm)
                .clients()
                .findByClientId(clientId)
                .getFirst();

        String kcClientId = client.getId();
        logger.debug("Client ID: " + kcClientId);

        // find target role by client id
        RoleRepresentation roleRepresentation = kc
                .realm(realm)
                .clients()
                .get(kcClientId)
                .roles()
                .get(role.toString())
                .toRepresentation();

        // add target role to user
        RoleScopeResource clientRoles = kc
                .realm(realm)
                .users()
                .get(keycloakUserId)
                .roles()
                .clientLevel(kcClientId);

        // remove existing roles
        List<RoleRepresentation> currentRoles = clientRoles.listAll();
        if (!currentRoles.isEmpty()) {
            clientRoles.remove(currentRoles);
        }

        // add new role
        clientRoles.add(List.of(roleRepresentation));

    }

    public void deleteKeycloakUser(String keycloakUserId) {
        try (Response deleteResponse = kc
                .realm(realm)
                .users()
                .delete(keycloakUserId)) {
            if (deleteResponse.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
                throw new KeycloakDeleteFailedException(String.valueOf(deleteResponse.getStatus()));
            }
        }
    }

    public void deleteAllKeycloakUsers() {
        List<UserRepresentation> users = kc
                .realm(realm)
                .users()
                .list();
        users.forEach(user -> deleteKeycloakUser(user.getId()));
    }

}
