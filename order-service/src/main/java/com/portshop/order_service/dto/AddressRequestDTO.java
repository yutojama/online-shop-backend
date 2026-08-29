package com.portshop.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDTO {

    @NotBlank(message = "Firstname required")
    private String firstName;

    @NotBlank(message = "Lastname required")
    private String lastName;

    @NotBlank(message = "Street required")
    private String street;

    @NotBlank(message = "City required")
    private String city;

    @NotBlank(message = "Postal code required")
    private String postalCode;

    @NotBlank(message = "Country required")
    private String country;

}
