package com.portshop.api_gateway.controller;

import com.portshop.api_gateway.data.DataAgent;
import com.portshop.api_gateway.dto.UserRequestDTO;
import com.portshop.api_gateway.service.UserDbService;
import com.portshop.api_gateway.service.UserDebugService;
import com.portshop.api_gateway.service.UserService;
import com.portshop.api_gateway.userEnum.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserDebugService userDebugService;
    private final DataAgent dataAgent;
    private final UserDbService userDbService;
    private final UserService userService;

    public AdminController(UserDebugService userDebugService, DataAgent dataAgent, UserDbService userDbService, UserService userService) {
        this.userDebugService = userDebugService;
        this.dataAgent = dataAgent;
        this.userDbService = userDbService;
        this.userService = userService;
    }

    // debugging controllers
    @GetMapping("/debug-keycloak")
    public ResponseEntity<?> getKeycloakClientInfo() {
        return  ResponseEntity.ok().body(userDebugService.getKeycloakClientInfo());
    }

    @DeleteMapping("/reset-all")
    public ResponseEntity<?> deleteAllUsers() {
        userDebugService.deleteAllUsers();
        userService.createUser(
                UserRequestDTO.builder()
                        .email("admin@email.com")
                        .password("password")
                        .firstName("admin")
                        .lastName("admin")
                        .phone("101")
                        .build(),
                Role.ADMIN
        );
        return ResponseEntity.ok().body("All users deleted successfully");
    }

    // generate data
    @GetMapping("/generate-data/{amount}")
    public ResponseEntity<?> generateUserData(@PathVariable Integer amount) {
        dataAgent.generateUserData(amount);
        dataAgent.generateAddressData();
        return ResponseEntity.ok().body("Generate user data and address successfully");
    }


    @PostMapping("/{userId}/role/{role}")
    public ResponseEntity<?> updateUserRole(@PathVariable String userId, @PathVariable Role role) {
        userService.updateUserRole(userId,role);
        return ResponseEntity.ok().body("Role change successfully: "+role);
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().body("User deleted successfully: "+userId);
    }
}
