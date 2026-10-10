package com.security.otp.core.dtos.otp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.security.otp.core.dtos.generator.GenerateCodeReq;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Duration;

public record CreateOtpReq(
        @JsonProperty("id")
        @NotNull String id,

        @JsonProperty("lifetime")
        @NotNull Duration lifetime,

        @JsonProperty("attempts")
        @Positive int attempts,

        @JsonProperty("generateCodeReq")
        @NotNull GenerateCodeReq generateCodeReq
) {}