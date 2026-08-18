package com.portshop.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain keycloakSecurityFilterChain(HttpSecurity http) {
        /* disable all spring security setups since we use keycloak */
        http
                .authorizeHttpRequests(auth->auth
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2->
                        oauth2.jwt(Customizer.withDefaults())
                )
                .sessionManagement(session->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic(httpBasic->httpBasic.disable())
                .formLogin(formLogin->formLogin.disable())
                .cors(cors->cors.disable())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
