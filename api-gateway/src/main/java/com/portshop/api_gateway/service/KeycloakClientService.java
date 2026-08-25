package com.portshop.api_gateway.service;

import com.portshop.api_gateway.dto.RegisterRequestDTO;
import com.portshop.api_gateway.exception.KeycloakCreateUserException;
import com.portshop.api_gateway.exception.KeycloakDeleteFailedException;
import com.portshop.api_gateway.userEnum.Role;
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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
public class KeycloakClientService {

    private final Logger logger = LoggerFactory.getLogger(KeycloakClientService.class);
    private final Keycloak kc;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    public KeycloakClientService(Keycloak kc) {
        this.kc = kc;
    }

    // return keycloak id
    public Mono<String> createKeycloakUser(RegisterRequestDTO registerRequestDTO) {
        // create keycloak user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(registerRequestDTO.resolveUsername());
        user.setEmail(registerRequestDTO.getEmail());
        user.setFirstName(registerRequestDTO.getFirstName());
        user.setLastName(registerRequestDTO.getLastName());
        user.setEnabled(true);
        user.setEmailVerified(false);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(registerRequestDTO.getPassword());
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));

        Mono<Response> responseMono = Mono.fromCallable(()->
                 kc.realm(realm)
                         .users()
                         .create(user))
                .subscribeOn(Schedulers.boundedElastic());

        // get keycloak id from response
        return responseMono.handle((response, sink) -> {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                sink.error(new KeycloakCreateUserException("Could not create Keycloak user: " + String.valueOf(response.getStatus())));
                return;
            }
            String location = response.getLocation().toString();
            String kId = location.substring(location.lastIndexOf("/") + 1);
            sink.next(kId);
        });

    }

    public Mono<Void> updateUserRole(String keycloakUserId, Role role) {
        // get client id
        Mono<String> kcClientIdMono = Mono.fromCallable(()-> {
            ClientRepresentation client = kc.realm(realm)
                    .clients()
                    .findByClientId(clientId)
                    .getFirst();
            return client.getId();
        }).subscribeOn(Schedulers.boundedElastic());

        // find target role by client id
        Mono<RoleRepresentation> roleRepresentationMono = kcClientIdMono.flatMap(kcClientId -> {
            return Mono.fromCallable(()->{
                return kc
                    .realm(realm)
                    .clients()
                    .get(kcClientId)
                    .roles()
                    .get(role.toString())
                    .toRepresentation();
            });
        });

        // get user roles
        Mono<RoleScopeResource> clientRolesMono = kcClientIdMono.flatMap(kcId -> {
            return Mono.fromCallable(()->{
                return kc
                    .realm(realm)
                    .users()
                    .get(keycloakUserId)
                    .roles()
                    .clientLevel(kcId);
            }).subscribeOn(Schedulers.boundedElastic());
        });

        // remove existing roles
        Mono<RoleScopeResource> emptyRoles = clientRolesMono.map(clientRoles -> {
            List<RoleRepresentation> currentRoles = clientRoles.listAll();
            if (!currentRoles.isEmpty()) {
                clientRoles.remove(currentRoles);
            }
            return clientRoles;
        });

        // add new role
        return emptyRoles.flatMap(clientRoles -> roleRepresentationMono.map(
                roleRepresentation -> {
                    clientRoles.add(List.of(roleRepresentation));
                    return clientRoles;
                }
        )).then();

    }

    public Mono<Void> deleteKeycloakUser(String keycloakUserId) {
        return Mono.fromRunnable(()->{
            try (Response deleteResponse = kc
                    .realm(realm)
                    .users()
                    .delete(keycloakUserId)) {
                if (deleteResponse.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
                    throw new KeycloakDeleteFailedException("Could not delete Keycloak user: " + String.valueOf(deleteResponse.getStatus()));
                }
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<Void> deleteAllKeycloakUsers() {
        // keycloak users are paginated, so delete in batch
        int batchSize = 100;

        return Mono.fromRunnable(()->{
            while (true){
                List<UserRepresentation> users = kc
                        .realm(realm)
                        .users()
                        .list(0, batchSize);
                if(users.isEmpty()){ break;}
                users.forEach(user -> deleteKeycloakUser(user.getId()));
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();

    }

}
