package com.portshop.api_gateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDTO {

    @NotBlank(message = "Username required for login")
    private String username;
    @NotBlank(message = "Password required for login")
    private String password;

}
