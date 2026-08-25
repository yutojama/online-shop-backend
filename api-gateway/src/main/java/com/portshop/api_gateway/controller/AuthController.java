package com.portshop.api_gateway.controller;

import com.portshop.api_gateway.dto.LoginRequestDTO;
import com.portshop.api_gateway.dto.RegisterRequestDTO;
import com.portshop.api_gateway.service.AuthDbService;
import com.portshop.api_gateway.service.KeycloakAuthService;
import com.portshop.api_gateway.service.UserService;
import com.portshop.api_gateway.userEnum.Role;
import jakarta.validation.Valid;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    // not blocking
    private final KeycloakAuthService keycloakAuthService;
    private final AuthDbService authDbService;
    private final ServerSecurityContextRepository serverSecurityContextRepository;

    public AuthController(UserService userService, KeycloakAuthService keycloakAuthService, AuthDbService authDbService, ServerSecurityContextRepository serverSecurityContextRepository) {
        this.userService = userService;
        this.keycloakAuthService = keycloakAuthService;
        this.authDbService = authDbService;
        this.serverSecurityContextRepository = serverSecurityContextRepository;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(
            ServerWebExchange exchange,
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {

        Mono<String> sessionId = exchange.getSession()
                .map(WebSession::getId);

        // save jwt and session ID in auth db
        Mono<AccessTokenResponse> accessTokenResponseMono = keycloakAuthService.getAccessToken(loginRequestDTO);
        Mono<String> keycloakId = accessTokenResponseMono.map(keycloakAuthService::getKeycloakId);
        Mono<String> authSessionId = Mono.zip(sessionId, keycloakId, accessTokenResponseMono)
                .flatMap(tuple -> authDbService.saveAuthSession(
                        tuple.getT1(), tuple.getT2(), tuple.getT3()
                ));

        // create Authentication
        Mono<String> roleMono = accessTokenResponseMono.map(keycloakAuthService::getKeycloakRole);
        Mono<Authentication> authenticationMono = Mono.zip(keycloakId, roleMono)
                .map(tuple ->
                        new UsernamePasswordAuthenticationToken(tuple.getT1(), null, List.of(new SimpleGrantedAuthority("ROLE_" + tuple.getT2())))
                );

        // create spring security context and save it
        return authenticationMono.flatMap(auth -> {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            return serverSecurityContextRepository.save(exchange,context)
                    .then(authSessionId.map(
                            asId -> ResponseEntity.ok().body("Login successful, auth session ID: "+asId)
                    ));
        });

    }

    @PostMapping("/logout")
    public Mono<String> logout(ServerWebExchange exchange) {
        return exchange.getSession()
                .doOnNext(session ->
                        logger.info("WebSession ID: {}", session.getId())
                ).thenReturn("logout");
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<?>> createUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return userService.createUser(registerRequestDTO, Role.CUSTOMER).flatMap(userId ->
                        Mono.just(ResponseEntity.ok().body("User created successfully"+userId))
                );
    }

}
