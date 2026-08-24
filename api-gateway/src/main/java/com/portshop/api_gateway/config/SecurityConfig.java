package com.portshop.api_gateway.config;

import com.portshop.api_gateway.service.AuthDbService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.FilterSupplier;
import org.springframework.cloud.gateway.server.mvc.filter.SimpleFilterSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${keycloak.client-id}")
    private String clientId;

    @Bean
    SecurityFilterChain keycloakSecurityFilterChain(
            HttpSecurity http,
            SecurityContextRepository securityContextRepository

    ) {
        /* disable all spring security setups since we use keycloak */
        http
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/admin/auth/**").hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository)
                )
                .sessionManagement(session->session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .httpBasic(httpBasic->httpBasic.disable())
                .formLogin(formLogin->formLogin.disable())
                .cors(cors->cors.disable())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // the session id only needs httpSession to validate,
    // but still need security context for authentication (who is logged in) for @preAuthorized and .anyRequest().authenticated()
    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

}
