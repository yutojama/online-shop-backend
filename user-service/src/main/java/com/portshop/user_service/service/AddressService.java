package com.portshop.user_service.service;

import com.portshop.user_service.dto.AddressMapper;
import com.portshop.user_service.dto.AddressCreateRequestDTO;
import com.portshop.user_service.dto.AddressResponseDTO;
import com.portshop.user_service.exception.AddressNotFoundException;
import com.portshop.user_service.model.Address;
import com.portshop.user_service.model.ApiUser;
import com.portshop.user_service.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AddressService {

    private final UserDbService userDbService;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressService(UserDbService userDbService, AddressRepository addressRepository, AddressMapper addressMapper) {
        this.userDbService = userDbService;
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    public AddressResponseDTO saveAddress(AddressCreateRequestDTO addressDTO) {
        ApiUser user = userDbService.getApiUser(addressDTO.getUserId());
        Address address = addressMapper.requestDTOToAddress(addressDTO);
        user.addAddress(address);
        Address savedAddress = addressRepository.save(address);
        return addressMapper.addressToResponseDTO(savedAddress);
    }

    public Address getAddress(String addressId){
        Optional<Address> address = addressRepository.findById(UUID.fromString(addressId));
        if(address.isPresent()){
            return address.get();
        }else {
            throw new AddressNotFoundException("Address not found: " + addressId);
        }
    }

    public List<AddressResponseDTO> getAddresses(String userId){
        return userDbService.getApiUser(userId).getAddresses().stream().map(addressMapper::addressToResponseDTO).toList();
    }

    public void deleteAddress(String addressId) {
        Address address = getAddress(addressId);
        addressRepository.delete(address);
    }
}
