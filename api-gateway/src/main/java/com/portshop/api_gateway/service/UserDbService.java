package com.portshop.api_gateway.service;

import com.portshop.api_gateway.dto.UserRequestDTO;
import com.portshop.api_gateway.userEnum.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserDbService {

    // return user id
    public String createUser(String keycloakUserId, Role role, UserRequestDTO userRequestDTO) {
        // create customer and save in db

        return null;
    }

    // return keycloak id
    public String updateUserRole(String userId, Role role) {
        return null;
    }

    public Role getRole(String userId) {

    }

    public ApiUser saveApiUser(ApiUser apiUser) {
        return userRepository.save(apiUser);
    }

    // return keycloak id
    public String deleteUser(String userId) {

    }

    public void deleteAllUsers() {

    }
}
