package com.portshop.user_service.data;

import com.portshop.user_service.dto.AddressRequestDTO;
import com.portshop.user_service.dto.UserRequestDTO;
import com.portshop.user_service.model.ApiUser;
import com.portshop.user_service.service.AddressService;
import com.portshop.user_service.service.UserDbService;
import com.portshop.user_service.service.UserService;
import com.portshop.user_service.userEnum.Role;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataAgent {

    private final Faker faker;
    private final UserService userService;
    private final UserDbService userDbService;
    private final AddressService addressService;

    public DataAgent(UserService userService, UserDbService userDbService, AddressService addressService) {
        this.userService = userService;
        this.userDbService = userDbService;
        this.faker = new Faker();
        this.addressService = addressService;
    }

    public void generateUserData(Integer nr) {
        for (int i=0; i<nr; i++) {
            String firstname = faker.name().firstName().replace("'","");
            String lastname = faker.name().lastName().replace("'","");
            String domain = faker.internet().emailAddress();
            String email = firstname.toLowerCase() + '_' +lastname.toLowerCase() + '@'
                    + domain.split("@")[1];
            UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                    .email(email)
                    .password("password")
                    .firstName(firstname)
                    .lastName(lastname)
                    .phone(faker.phoneNumber().phoneNumber())
                    .build();
            Role val = ThreadLocalRandom.current().nextBoolean()?Role.CUSTOMER:Role.SHOP;
            userService.createUser(userRequestDTO, val);
        }
    }

    public void generateAddressData() {
        List<ApiUser> users = userDbService.getAllUsers();
        users.forEach(user -> {
            for (int i = 0; i < 3; i++) {
                if(ThreadLocalRandom.current().nextBoolean()){
                    AddressRequestDTO addressRequestDTO = AddressRequestDTO.builder()
                            .userId(user.getId().toString())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .street(faker.address().streetAddress())
                            .city(faker.address().city())
                            .postalCode(faker.address().zipCode())
                            .country(faker.address().country())
                            .build();
                    addressService.saveAddress(addressRequestDTO);
                }
            }
        });
    }
}
