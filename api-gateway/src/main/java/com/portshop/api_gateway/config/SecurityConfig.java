package com.portshop.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;


@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain keycloakSecurityFilterChain(
            ServerHttpSecurity http,
            ServerSecurityContextRepository contextRepository

    ) {
        // disable all spring security setups since we use keycloak
        http
                .authorizeExchange(exchanges->exchanges
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/admin/auth/**").hasRole("ADMIN")

                        .anyExchange()
                        .authenticated()
                )
                .securityContextRepository(contextRepository)
                .httpBasic(httpBasic->httpBasic.disable())
                .formLogin(formLogin->formLogin.disable())
                .cors(cors->cors.disable())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    /** the session id only needs httpSession to validate,
     but still need security context for authentication (who is logged in) for @preAuthorized and .anyRequest().authenticated()
    */
    @Bean
    ServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }

}
