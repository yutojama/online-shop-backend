package com.portshop.api_gateway.controller;

import com.portshop.api_gateway.dto.LoginRequestDTO;
import com.portshop.api_gateway.dto.RegisterRequestDTO;
import com.portshop.api_gateway.service.AuthDbService;
import com.portshop.api_gateway.service.KeycloakAuthService;
import com.portshop.api_gateway.service.UserService;
import com.portshop.api_gateway.userEnum.Role;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.keycloak.representations.AccessTokenResponse;
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
    private final AuthDbService authDbService;

    public AuthController(UserService userService, KeycloakAuthService keycloakAuthService, AuthDbService authDbService) {
        this.userService = userService;
        this.keycloakAuthService = keycloakAuthService;
        this.authDbService = authDbService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            HttpSession httpSession,
            @Valid @RequestBody LoginRequestDTO loginRequestDTO
    ) {
        String sessionId = httpSession.getId();
        AccessTokenResponse accessTokenResponse = keycloakAuthService.getAccessToken(loginRequestDTO);
        String keycloakId = keycloakAuthService.getKeycloakId(accessTokenResponse);
        String authSessionId = authDbService.saveAuthSession(sessionId, keycloakId, accessTokenResponse);
        return ResponseEntity.ok().body("Login successful: "+authSessionId);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("Logout successful");
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        String userId = userService.createUser(registerRequestDTO, Role.CUSTOMER);
        return ResponseEntity.ok().body("Customer created successfully: "+userId);
    }

}
