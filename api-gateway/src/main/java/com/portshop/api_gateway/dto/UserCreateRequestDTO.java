package com.portshop.api_gateway.dto;

import com.portshop.api_gateway.userEnum.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequestDTO {

    @NotBlank(message = "Keycloak ID is required")
    private String keycloakId;

    @NotBlank(message = "Role is required")
    private Role role;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Firstname is required")
    private String firstName;

    @NotBlank(message = "Lastname is required")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Username is required")
    private String username;

}
