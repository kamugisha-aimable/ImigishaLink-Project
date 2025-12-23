package com.imigishalink.auth;

import com.imigishalink.users.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private String phoneNumber;
    
    private Role role = Role.USER; // Default to USER, can be USER or NGO
    
    // Location fields for user registration
    private Long locationId; // Location ID if location already exists in database
    private String province;
    private String district;
    private String sector;
    private String cell;
    private String village;
}
