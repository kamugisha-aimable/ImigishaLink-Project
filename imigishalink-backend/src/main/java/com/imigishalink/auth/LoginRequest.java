package com.imigishalink.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
    private String otpEmail; // Optional: email to receive OTP (defaults to login email if not provided)
}