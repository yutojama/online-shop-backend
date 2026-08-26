package com.portshop.api_gateway.data;

import com.portshop.api_gateway.dto.RegisterRequestDTO;
import com.portshop.api_gateway.service.UserDbService;
import com.portshop.api_gateway.service.UserService;
import com.portshop.api_gateway.userEnum.Role;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
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

    public Mono<Void> generateUserData(Integer shopNr, Integer customerNr) {
        List<RegisterRequestDTO> shops = getUserData(shopNr);
        List<RegisterRequestDTO> customers = getUserData(customerNr);
        return Flux.fromIterable(shops)
                .flatMap(shop->
                        userService.createUser(shop, Role.SHOP)
                ).then(
                        Flux.fromIterable(customers).flatMap(customer ->
                                userService.createUser(customer, Role.CUSTOMER)
                        ).then()
                );
    }

    public List<RegisterRequestDTO> getUserData(Integer nr) {
        List<RegisterRequestDTO> registerRequestDTOList = new ArrayList<>();
        for (int i=0; i<nr; i++) {
            String firstname = faker.name().firstName().replace("'","");
            String lastname = faker.name().lastName().replace("'","");
            String domain = faker.internet().emailAddress();
            String email = firstname.toLowerCase() + '_' +lastname.toLowerCase() + '@'
                    + domain.split("@")[1];
            RegisterRequestDTO registerRequestDTO = RegisterRequestDTO.builder()
                    .email(email)
                    .password("password")
                    .firstName(firstname)
                    .lastName(lastname)
                    .phone(faker.phoneNumber().phoneNumber())
                    .build();
            registerRequestDTOList.add(registerRequestDTO);
        }
        return registerRequestDTOList;
    }

    public Mono<Void> deleteAllUsers() {
        return userService.deleteAllUsers();
    }

}
