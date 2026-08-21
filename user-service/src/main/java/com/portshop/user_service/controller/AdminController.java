package com.portshop.user_service.controller;

import com.portshop.user_service.data.DataAgent;
import com.portshop.user_service.dto.UserRequestDTO;
import com.portshop.user_service.service.UserDebugService;
import com.portshop.user_service.userEnum.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserDebugService userDebugService;
    private final DataAgent dataAgent;
    private final UserService userService;

    public AdminController(UserDebugService userDebugService, DataAgent dataAgent, UserService userService) {
        this.userDebugService = userDebugService;
        this.dataAgent = dataAgent;
        this.userService = userService;
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
        dataAgent.generateUserData();
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
