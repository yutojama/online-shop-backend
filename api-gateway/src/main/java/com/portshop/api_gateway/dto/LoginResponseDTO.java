package com.portshop.api_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String userId;
    private String access_token;
    private String refresh_token;
    private String expires_in;
}
