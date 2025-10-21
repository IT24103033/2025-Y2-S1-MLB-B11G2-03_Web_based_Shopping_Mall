package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // Allow EVERYTHING for testing
            )
            .csrf(csrf -> csrf.disable())  // Disable CSRF
            .formLogin(form -> form.disable())  // Disable login
            .httpBasic(basic -> basic.disable())  // Disable basic auth
            .logout(logout -> logout.disable());  // Disable logout
            
        return http.build();
    }
}
