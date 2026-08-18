package com.portshop.user_service.controller;

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
    private final UserDebugService userDebugService;

    public UserController(UserService userService, UserDebugService userDebugService1) {
        this.userService = userService;
        this.userDebugService = userDebugService1;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        String userId = userService.createUser(userRequestDTO, Role.CUSTOMER);
        return ResponseEntity.ok().body("Customer created successfully: "+userId);
    }

    @PostMapping("/{userId}/role/{role}")
    public ResponseEntity<?> updateUserRole(@PathVariable String userId, @PathVariable Role role) {
        userService.updateUserRole(userId,role);
        return ResponseEntity.ok().body("Role change successfully: "+role);
    }


    // debugging controllers
    @GetMapping("/debug-keycloak")
    public ResponseEntity<?> getKeycloakClientInfo() {
        return  ResponseEntity.ok().body(userDebugService.getKeycloakClientInfo());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().body("User deleted successfully: "+userId);
    }

    @DeleteMapping("/reset-all")
    public ResponseEntity<?> deleteAllUsers() {
        userDebugService.deleteAllUsers();
        return ResponseEntity.ok().body("All users deleted successfully");
    }
}
