package com.portshop.user_service.dto;

import com.portshop.user_service.userEnum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiUserResponseDTO {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
}
