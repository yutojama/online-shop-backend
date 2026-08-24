package com.portshop.api_gateway.controller;

import com.portshop.api_gateway.dto.LoginRequestDTO;
import com.portshop.api_gateway.dto.RegisterRequestDTO;
import com.portshop.api_gateway.service.AuthDbService;
import com.portshop.api_gateway.service.KeycloakAuthService;
import com.portshop.api_gateway.service.UserService;
import com.portshop.api_gateway.userEnum.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final KeycloakAuthService keycloakAuthService;
    private final AuthDbService authDbService;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(UserService userService, KeycloakAuthService keycloakAuthService, AuthDbService authDbService, SecurityContextRepository securityContextRepository) {
        this.userService = userService;
        this.keycloakAuthService = keycloakAuthService;
        this.authDbService = authDbService;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @RequestBody LoginRequestDTO loginRequestDTO
    ) {
        // because of permitAll() on /login, there is no session created for this request,
        // to the session ID will be changed for a new login to avoid session ID fixation
        HttpSession session = request.getSession(true);
        request.changeSessionId();
        String sessionId = session.getId();

        // save jwt and session ID in auth db
        AccessTokenResponse accessTokenResponse = keycloakAuthService.getAccessToken(loginRequestDTO);
        String keycloakId = keycloakAuthService.getKeycloakId(accessTokenResponse);
        String authSessionId = authDbService.saveAuthSession(sessionId, keycloakId, accessTokenResponse);

        // create Authentication
        String role = keycloakAuthService.getKeycloakRole(accessTokenResponse);
        Authentication authentication = new UsernamePasswordAuthenticationToken(keycloakId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        // create spring security context and save it
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextRepository.saveContext(context,request,response);

        return ResponseEntity.ok().body("Login successful, auth session ID: "+authSessionId);
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
