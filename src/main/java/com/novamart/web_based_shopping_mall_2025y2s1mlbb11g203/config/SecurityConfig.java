package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.config;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller.CustomLoginSuccessHandler;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(CustomLoginSuccessHandler customLoginSuccessHandler, UserDetailsServiceImpl userDetailsService) {
        this.customLoginSuccessHandler = customLoginSuccessHandler;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/shop/**").hasAnyRole("SHOP_OWNER", "ADMIN")
                        .requestMatchers("/", "/register", "/login", "/forgot-password").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/home").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")  // Match the login form's email field
                        .successHandler(customLoginSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());
        return http.build();
    }
}