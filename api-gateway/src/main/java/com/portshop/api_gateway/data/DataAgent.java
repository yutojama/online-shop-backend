package com.portshop.api_gateway.data;

import com.portshop.api_gateway.dto.UserRequestDTO;
import com.portshop.api_gateway.service.UserDbService;
import com.portshop.api_gateway.service.UserService;
import com.portshop.api_gateway.userEnum.Role;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataAgent {

    private final Faker faker;
    private final UserService userService;

    public DataAgent(UserService userService, UserDbService userDbService) {
        this.userService = userService;
        this.faker = new Faker();
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

    }
}
