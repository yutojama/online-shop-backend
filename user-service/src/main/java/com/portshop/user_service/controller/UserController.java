package com.portshop.user_service.controller;

import com.portshop.user_service.userEnum.Role;
import com.portshop.user_service.dto.UserRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        String userId = userService.createUser(userRequestDTO, Role.CUSTOMER);
        return ResponseEntity.ok().body("Customer created successfully: "+userId);
    }

}
