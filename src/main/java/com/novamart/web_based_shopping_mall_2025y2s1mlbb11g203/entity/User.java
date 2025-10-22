package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User Entity - Represents users in the system
 * Supports authentication and role-based access control
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be less than 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be less than 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "address")
    private String address;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.CUSTOMER;
    
    @Column(name = "created_date")
    private java.time.LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private java.time.LocalDateTime updatedDate;
    
    // Role enum for user types
    public enum Role {
        CUSTOMER, SHOP_OWNER, ADMIN
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdDate = java.time.LocalDateTime.now();
        updatedDate = java.time.LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedDate = java.time.LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public java.time.LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(java.time.LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public java.time.LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(java.time.LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}
