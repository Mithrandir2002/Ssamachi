package com.earthquake.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // TODO: this is a permitAll-everything placeholder so the app boots and is reachable
    // during development. Replace with the real rules before this goes anywhere near
    // production, and wire in JwtAuthenticationFilter (currently NOT added to the chain):
    //   - permitAll:      GET /api/earthquakes/**, GET /api/stats/**, /actuator/health, /ws/**
    //   - authenticated:  /api/subscriptions/**, /api/reports/**
    //   - hasRole ADMIN:  /api/admin/**
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
