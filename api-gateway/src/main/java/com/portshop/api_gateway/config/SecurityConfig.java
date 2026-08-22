package com.portshop.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
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

import java.util.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${keycloak.client-id}")
    private String clientId;

    @Bean
    SecurityFilterChain keycloakSecurityFilterChain(HttpSecurity http) {
        /* disable all spring security setups since we use keycloak */
        http
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/users/logout").permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationTokenConverter()))
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

    // this bean adds ROLE_ in front of all custom roles, because spring security need this prefix and otherwise invalid
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationTokenConverter() {
        return jwt -> {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                Map<String, Object> client =  (Map<String, Object>) resourceAccess.get(clientId);

                if (client != null) {
                    Collection<String> roles = (Collection<String>) client.get("roles");

                    if (roles != null) {
                        roles.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                    }
                }
            }
            return new JwtAuthenticationToken(jwt, grantedAuthorities);
        };
    }
}
