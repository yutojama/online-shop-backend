package com.portshop.user_service.controller;

import com.portshop.user_service.dto.AddressCreateRequestDTO;
import com.portshop.user_service.dto.AddressResponseDTO;
import com.portshop.user_service.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/get-user-address/{userId}")
    public ResponseEntity<List<AddressResponseDTO>> getAddress(@PathVariable("userId") String userId){
        return ResponseEntity.ok().body(addressService.getAddresses(userId));
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
