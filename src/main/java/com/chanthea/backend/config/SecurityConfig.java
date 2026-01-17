package com.chanthea.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Required for POST/PUT/DELETE
                .authorizeHttpRequests(auth -> auth
                        // Allow specific path
                        .requestMatchers("/api/v1/deploy/**").permitAll()
                        // Or allow everything
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}