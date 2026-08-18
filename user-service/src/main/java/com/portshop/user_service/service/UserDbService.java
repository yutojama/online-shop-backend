package com.portshop.user_service.service;

import com.portshop.user_service.dto.UserRequestDTO;
import com.portshop.user_service.exception.ApiUserNotFoundException;
import com.portshop.user_service.model.ApiUser;
import com.portshop.user_service.repository.UserRepository;
import com.portshop.user_service.userEnum.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserDbService {

    private final UserRepository userRepository;

    public UserDbService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // return user id
    public String createUser(String keycloakUserId, Role role, UserRequestDTO userRequestDTO) {
        // create customer and save in db
        ApiUser newApiUser = ApiUser.builder()
                .keycloakUserId(keycloakUserId)
                .role(role)
                .email(userRequestDTO.getEmail())
                .firstName(userRequestDTO.getFirstName())
                .lastName(userRequestDTO.getLastName())
                .phone(userRequestDTO.getPhone())
                .build();
        ApiUser savedApiUser = saveApiUser(newApiUser);
        return savedApiUser.getId().toString();
    }

    public void updateUserRole(String userId, Role role) {
        Optional<ApiUser> apiUser = userRepository.getApiUserById(UUID.fromString(userId));
        if(apiUser.isPresent()) {
            apiUser.get().setRole(role);
            userRepository.save(apiUser.get());
        }else {
            throw new ApiUserNotFoundException("User not found: "+userId);
        }
    }

    public String getKeycloakUserId(String userId) {
        Optional<ApiUser> apiUser = userRepository.getApiUserById(UUID.fromString(userId));
        if(apiUser.isPresent()) {
            return apiUser.get().getKeycloakUserId();
        }else{
            throw new ApiUserNotFoundException("User not found: "+userId);
        }
    }

    public ApiUser saveApiUser(ApiUser apiUser) {
        return userRepository.save(apiUser);
    }

}
