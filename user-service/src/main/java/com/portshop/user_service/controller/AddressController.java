package com.portshop.user_service.controller;

import com.portshop.user_service.dto.AddressCreateRequestDTO;
import com.portshop.user_service.dto.AddressMapper;
import com.portshop.user_service.dto.AddressResponseDTO;
import com.portshop.user_service.model.ApiUser;
import com.portshop.user_service.service.AddressService;
import com.portshop.user_service.service.UserDbService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final UserDbService userService;
    private final AddressMapper addressMapper;

    public AddressController(AddressService addressService, UserDbService userService, AddressMapper addressMapper) {
        this.addressService = addressService;
        this.userService = userService;
        this.addressMapper = addressMapper;
    }

    @GetMapping("/get-user-address/{userId}")
    public ResponseEntity<List<AddressResponseDTO>> getAddress(@PathVariable("userId") String userId){
        return ResponseEntity.ok().body(addressService.getAddresses(userId));
    }

    @GetMapping("/get-my-address")
    public ResponseEntity<List<AddressResponseDTO>> getAddress(
            Authentication authentication
    ){
        String kcId = authentication.getName();
        ApiUser apiUser = userService.getApiUserByKeycloakId(kcId);
        List<AddressResponseDTO> AddressDTOs = apiUser.getAddresses().stream().map(addressMapper::addressToResponseDTO).toList();
        return ResponseEntity.ok().body(AddressDTOs);
    }

    @PostMapping("/add-address")
    public ResponseEntity<?> addAddress(@Valid @RequestBody AddressCreateRequestDTO addressCreateRequestDTO){
        return ResponseEntity.ok().body(addressService.saveAddress(addressCreateRequestDTO));
    }

    // should only delete user's own addresses
    @DeleteMapping("/delete-address/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable String addressId){
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok().body("Address has been deleted: "+addressId);
    }

}
