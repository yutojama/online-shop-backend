package com.portshop.user_service.dto;

import com.portshop.user_service.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public Address requestDTOToAddress(AddressRequestDTO addressDTO){
        return Address.builder()
                .firstName(addressDTO.getFirstName())
                .lastName(addressDTO.getLastName())
                .street(addressDTO.getStreet())
                .city(addressDTO.getCity())
                .postalCode(addressDTO.getPostalCode())
                .country(addressDTO.getCountry())
                .build();
    }

    public AddressResponseDTO addressToResponseDTO(Address address){
        return AddressResponseDTO.builder()
                .id(address.getId().toString())
                .userId(address.getUser().getId().toString())
                .firstName(address.getFirstName())
                .lastName(address.getLastName())
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();
    }
}
