package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.User;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * UserDetailsService implementation for Spring Security
 * Loads user-specific data for authentication
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Load user by username for authentication
     * This method is called by Spring Security during login
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Build and return Spring Security UserDetails object
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())  // Should be BCrypt encoded
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * Convert user role to Spring Security authorities
     * Authorities are used for role-based access control
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // Spring Security expects role names to be prefixed with "ROLE_"
        String roleName = "ROLE_" + user.getRole().name();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }
}
