package com.portshop.user_service.controller;

import com.portshop.user_service.dto.UserCreateRequestDTO;
import com.portshop.user_service.model.ApiUser;
import com.portshop.user_service.service.UserDbService;
import com.portshop.user_service.userEnum.Role;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserDbController {

    private final UserDbService userDbService;

    public UserDbController(UserDbService userDbService) {
        this.userDbService = userDbService;
    }

    @PostMapping("/create")
    public String create(@Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        return userDbService.createUser(userCreateRequestDTO);
    }

    @PostMapping("/{userId}/role/{role}")
    public String updateUserRole(@PathVariable String userId, @PathVariable Role role) {
        return userDbService.updateUserRole(userId,role);
    }

    @GetMapping("/get-role/{userId}")
    public Role getRole(@PathVariable String userId) {
        return userDbService.getRole(userId);
    }

    @GetMapping("/get-user/{keycloakId}")
    public ApiUser getUser(@PathVariable String keycloakId) {
        return userDbService.getApiUserByKeycloakId(keycloakId);
    }

    @DeleteMapping("/delete-user/{userId}")
    public String deleteUser(@PathVariable String userId) {
        return userDbService.deleteUser(userId);
    }

}
