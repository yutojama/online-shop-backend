package com.portshop.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDTO {

    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String postalCode;
    private String country;

}
