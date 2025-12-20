package com.imigishalink.auth;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Integer expiresIn;
    private Map<String, Object> user;
}