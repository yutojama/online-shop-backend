package com.portshop.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;


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

                        .pathMatchers("/public/**").permitAll()

                        .anyExchange()
                        .authenticated()
                )
                .securityContextRepository(contextRepository)
                .httpBasic(httpBasic->httpBasic.disable())

                // Return plain 401 than WWW-Authenticate which results in basic login modal at 401
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(
                                (exchange, exception) -> {
                                    exchange.getResponse().setStatusCode(
                                            HttpStatus.UNAUTHORIZED
                                    );

                                    return exchange.getResponse().setComplete();
                                }
                        )
                )

                .formLogin(formLogin->formLogin.disable())
//                .cors(cors -> cors.disable())
//                .csrf(csrf -> csrf.disable());
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()); // should be disabled when using intellij http files
//                .csrf(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /** the session id only needs httpSession to validate,
     but still need security context for authentication (who is logged in) for @preAuthorized and .anyRequest().authenticated()
    */
    @Bean
    ServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }

}
