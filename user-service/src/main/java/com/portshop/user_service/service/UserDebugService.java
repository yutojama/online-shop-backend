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
    private final UserDbService userDbService;

    public UserDebugService(UserDbService userDbService) {
        this.userDbService = userDbService;
    }

    public void deleteAllUsers() {
        userDbService.deleteAllUsers();
    }
}
