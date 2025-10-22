package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration
 * Configures authentication, authorization, and security settings for the application
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Password encoder bean - uses BCrypt hashing algorithm
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager bean
     * Handles authentication requests
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Security filter chain configuration
     * Defines which URLs require authentication and which are public
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF configuration - disable for now (can enable later with proper token handling)
            .csrf(csrf -> csrf.disable())
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public pages - accessible without authentication
                .requestMatchers("/", "/home", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                
                // API endpoints - temporarily allow for testing
                .requestMatchers("/api/products/**", "/api/cart/**").permitAll()
                
                // Checkout - temporarily allow for testing (will be restricted later)
                .requestMatchers("/api/checkout/**").permitAll()
                
                // Profile - requires authentication
                .requestMatchers("/profile/**", "/change-password").authenticated()
                
                // Admin pages - only accessible by ADMIN role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Store management - only accessible by SHOP_OWNER and ADMIN roles
                .requestMatchers("/store-management.html", "/api/stores/**").hasAnyRole("SHOP_OWNER", "ADMIN")
                
                // All other requests are public for now
                .anyRequest().permitAll()
            )
            
            // Login configuration
            .formLogin(form -> form
                .loginPage("/login")                    // Custom login page URL
                .loginProcessingUrl("/login")           // URL to submit login form
                .defaultSuccessUrl("/", true)           // Redirect after successful login
                .failureUrl("/login?error=true")        // Redirect after failed login
                .permitAll()
            )
            
            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/logout")                   // URL to trigger logout
                .logoutSuccessUrl("/login?logout=true") // Redirect after logout
                .invalidateHttpSession(true)            // Invalidate session
                .deleteCookies("JSESSIONID")            // Delete session cookie
                .permitAll()
            )
            
            // Session management
            .sessionManagement(session -> session
                .maximumSessions(1)                     // Only one session per user
                .maxSessionsPreventsLogin(false)        // Allow new login (invalidate old session)
            );

        return http.build();
    }
}
