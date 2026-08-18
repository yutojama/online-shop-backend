package com.portshop.user_service.service;

import com.portshop.user_service.repository.UserRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserDebugService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Keycloak kc;
    private final UserKeycloakService userKeycloakService;
    private final UserDbService userDbService;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.realm}")
    private String realm;

    public UserDebugService(Keycloak kc, UserKeycloakService userKeycloakService, UserDbService userDbService) {
        this.kc = kc;
        this.userKeycloakService = userKeycloakService;
        this.userDbService = userDbService;
    }

    public Map<String, String> getKeycloakClientInfo() {
        Map<String, String> keycloakClientInfo = new HashMap<>();

        // get Client ID by client
        log.info("Client: {}",clientId);
        keycloakClientInfo.put("Client", clientId);

        List<ClientRepresentation> clients = kc
                .realm(realm)
                .clients()
                .findAll();

        if (clients.isEmpty()) {
            log.info("No client found");
            keycloakClientInfo.put("Error", "No client found");
        }else{
            clients.forEach(client -> {
                log.info("Client ID: " + client.getId());
            });
        }

        // show realm
        log.info("Realm: " + realm);
        keycloakClientInfo.put("Realm", realm);

        // check if client sees realm
        RealmRepresentation realmRepresentation =
                kc.realm(realm).toRepresentation();
        log.info("If client sees realm: " + realmRepresentation.getRealm());
        keycloakClientInfo.put("If client sees realm", realmRepresentation.getRealm());

        return keycloakClientInfo;
    }

    public void deleteAllUsers() {
        userDbService.deleteAllUsers();
        userKeycloakService.deleteAllKeycloakUsers();
    }
}
