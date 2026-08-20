package com.portshop.user_service.repository;

import com.portshop.user_service.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    void deleteAddressById(UUID id);
}
