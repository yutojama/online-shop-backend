package com.portshop.user_service.controller;

import com.portshop.user_service.dto.ApiUserResponseDTO;
import com.portshop.user_service.dto.UserCreateRequestDTO;
import com.portshop.user_service.model.ApiUser;
import com.portshop.user_service.service.UserDbService;
import com.portshop.user_service.userEnum.Role;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserDbController {

    private final UserDbService userDbService;

    public UserDbController(UserDbService userDbService) {
        this.userDbService = userDbService;
    }

    // auth update required
    @PostMapping("/create")
    public String create(@Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        return userDbService.createUser(userCreateRequestDTO);
    }

    // auth update required
    @PostMapping("/{userId}/role/{role}")
    public String updateUserRole(@PathVariable String userId, @PathVariable Role role) {
        return userDbService.updateUserRole(userId,role);
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/get-me")
    public ResponseEntity<ApiUserResponseDTO> getMe(Authentication authentication) {
        String kcId = authentication.getName();
        ApiUser apiUser = userDbService.getApiUserByKeycloakId(kcId);
        return ResponseEntity.ok().body(
                ApiUserResponseDTO.builder()
                        .userId(apiUser.getId().toString())
                        .email(apiUser.getEmail())
                        .firstName(apiUser.getFirstName())
                        .lastName(apiUser.getLastName())
                        .phone(apiUser.getPhone())
                        .role(apiUser.getRole())
                        .build()
        );
    }

    // auth update required
    @GetMapping("/get-role/{userId}")
    public Role getRole(@PathVariable String userId) {
        return userDbService.getRole(userId);
    }

    // auth update required
    @GetMapping("/get-user/by-kcId/{keycloakId}")
    public ApiUser getUserByKcId(@PathVariable String keycloakId) {
        return userDbService.getApiUserByKeycloakId(keycloakId);
    }

    // auth update required
    /** returns a list of keycloak id */
    @GetMapping("/get-user/by-role/{role}")
    public List<String> getUserByRole(@PathVariable Role role) {
        return userDbService.getApiUserByRole(role);
    }

    // auth update required
    @DeleteMapping("/delete-user/{userId}")
    public String deleteUser(@PathVariable String userId) {
        return userDbService.deleteUser(userId);
    }

}
