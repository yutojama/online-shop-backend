package com.portshop.user_service.controller;

import com.portshop.user_service.data.DataAgent;
import com.portshop.user_service.dto.LogoutRequest;
import com.portshop.user_service.dto.UserLoginRequestDTO;
import com.portshop.user_service.dto.UserLoginResponseDTO;
import com.portshop.user_service.service.KeycloakAuthService;
import com.portshop.user_service.service.UserDebugService;
import com.portshop.user_service.service.UserService;
import com.portshop.user_service.userEnum.Role;
import com.portshop.user_service.dto.UserRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final KeycloakAuthService keycloakAuthService;

    public UserController(UserService userService, KeycloakAuthService keycloakAuthService) {
        this.userService = userService;
        this.keycloakAuthService = keycloakAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        return ResponseEntity.ok().body(keycloakAuthService.getAccessToken(userLoginRequestDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        keycloakAuthService.logout(request.getRefreshToken());
        return ResponseEntity.ok().body("Logout successful");
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        String userId = userService.createUser(userRequestDTO, Role.CUSTOMER);
        return ResponseEntity.ok().body("Customer created successfully: "+userId);
    }

}
