package com.portshop.user_service.controller;

import com.portshop.user_service.data.DataAgent;
import com.portshop.user_service.dto.UserCreateRequestDTO;
import com.portshop.user_service.service.UserDbService;
import com.portshop.user_service.userEnum.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
public class AdminController {

    private final DataAgent dataAgent;
    private final UserDbService userDbService;

    public AdminController(DataAgent dataAgent, UserDbService userDbService) {
        this.dataAgent = dataAgent;
        this.userDbService = userDbService;
    }

    @DeleteMapping("/reset-all")
    public ResponseEntity<?> deleteAllUsers() {
        userDbService.deleteAllUsers();
        return ResponseEntity.ok().body("All users deleted successfully");
    }

    // generate data
    @GetMapping("/generate-data/{amount}")
    public ResponseEntity<?> generateUserData(@PathVariable Integer amount) {
        dataAgent.generateUserData();
        dataAgent.generateAddressData();
        return ResponseEntity.ok().body("Generate user data and address successfully");
    }

}
