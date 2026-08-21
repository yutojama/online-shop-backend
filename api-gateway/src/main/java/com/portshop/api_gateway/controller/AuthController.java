package com.portshop.api_gateway.controller;

import com.portshop.api_gateway.dto.LogoutRequest;
import com.portshop.api_gateway.dto.UserLoginRequestDTO;
import com.portshop.api_gateway.dto.UserRequestDTO;
import com.portshop.api_gateway.service.KeycloakAuthService;
import com.portshop.api_gateway.service.UserService;
import com.portshop.api_gateway.userEnum.Role;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final KeycloakAuthService keycloakAuthService;

    public AuthController(UserService userService, KeycloakAuthService keycloakAuthService) {
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
