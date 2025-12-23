package com.imigishalink.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginOTPRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email; // Email where OTP was sent (for OTP verification)

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be exactly 6 digits")
    private String otp;
    
    // Optional: Login email (for user lookup if different from OTP email)
    @Email(message = "Login email must be valid")
    private String loginEmail;
}

