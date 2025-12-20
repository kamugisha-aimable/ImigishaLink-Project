package com.imigishalink.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginStepOneResponse {
    private Long challengeId;
    private String message;
}

