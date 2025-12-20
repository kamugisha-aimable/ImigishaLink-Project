package com.imigishalink.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotNull
    private Long challengeId;

    @NotBlank
    private String code;
}

