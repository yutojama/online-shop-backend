package com.portshop.api_gateway.controller;

import com.portshop.api_gateway.data.DataAgent;
import com.portshop.api_gateway.dto.RegisterRequestDTO;
import com.portshop.api_gateway.service.UserDbService;
import com.portshop.api_gateway.service.UserService;
import com.portshop.api_gateway.userEnum.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/auth")
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final DataAgent dataAgent;
    private final UserService userService;
    private final UserDbService userDbService;

    public AdminController(DataAgent dataAgent, UserService userService, UserDbService userDbService) {
        this.dataAgent = dataAgent;
        this.userService = userService;
        this.userDbService = userDbService;
    }

    /** this is the admin version to test if the web client could add valid token on request */
    @GetMapping("/me")
    public Mono<Object> getMe(Authentication authentication, ServerWebExchange exchange) {
        logger.info("calling /me, keycloak ID: {}", authentication.getName());

        return userDbService.getUser(authentication.getName());
    }

    @DeleteMapping("/reset-all")
    public Mono<ResponseEntity<?>> deleteAllUsers() {
        return userService.deleteAllUsers().then(
                userService.createUser(
                        RegisterRequestDTO.builder()
                                .email("admin@email.com")
                                .password("password")
                                .firstName("admin")
                                .lastName("admin")
                                .phone("101")
                                .build(),
                        Role.ADMIN
                ).then(
                        Mono.just(ResponseEntity.ok().body("All users deleted successfully"))
                )
        );
    }

    /** generate data */
    @GetMapping("/generate-data")
    public Mono<ResponseEntity<?>> generateUserData(@RequestParam Integer shop, @RequestParam Integer cost) {
        return dataAgent.generateUserData(shop,cost).then(
                dataAgent.generateAddressData().then(
                        Mono.just(ResponseEntity.ok().body("Generate user data and address successfully"))
                )
        );
    }


    @PostMapping("/{userId}/role/{role}")
    public Mono<ResponseEntity<?>> updateUserRole(@PathVariable String userId, @PathVariable Role role) {
        return userService.updateUserRole(userId,role).then(
                Mono.just(ResponseEntity.ok().body("Role updated successfully: "+role))
        );
    }

    @DeleteMapping("/delete-user/{userId}")
    public Mono<ResponseEntity<?>> deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId).then(
                Mono.just(ResponseEntity.ok().body("User deleted successfully: "+userId))
        );
    }
}
