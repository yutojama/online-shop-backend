package com.portshop.user_service.data;

import com.portshop.user_service.dto.AddressCreateRequestDTO;
import com.portshop.user_service.model.ApiUser;
import com.portshop.user_service.service.AddressService;
import com.portshop.user_service.service.UserDbService;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataAgent {

    private final Faker faker;
    private final UserDbService userDbService;
    private final AddressService addressService;

    public DataAgent(UserDbService userDbService, AddressService addressService) {
        this.userDbService = userDbService;
        this.faker = new Faker();
        this.addressService = addressService;
    }

    public void generateUserData() {
    }

    public void generateAddressData() {
        List<ApiUser> users = userDbService.getAllUsers();
        users.forEach(user -> {
            // generate no address for admin user
            if(user.getFirstName().equals("admin")){
                return;
            }
            // generate max. 3 addresses each user
            for (int i = 0; i < 3; i++) {
                if(ThreadLocalRandom.current().nextBoolean()){
                    AddressCreateRequestDTO addressCreateRequestDTO = AddressCreateRequestDTO.builder()
                            .userId(user.getId().toString())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .street(faker.address().streetAddress())
                            .city(faker.address().city())
                            .postalCode(faker.address().zipCode())
                            .country(faker.address().country())
                            .build();
                    addressService.saveAddress(addressCreateRequestDTO);
                }
            }
        });
    }

    public void deleteAddressData() {
        List<ApiUser> users = userDbService.getAllUsers();
        users.forEach(user -> {
            if(user.getFirstName().equals("admin")){return;}
            user.getAddresses().clear();
            userDbService.saveApiUser(user);
        });
    }

}
