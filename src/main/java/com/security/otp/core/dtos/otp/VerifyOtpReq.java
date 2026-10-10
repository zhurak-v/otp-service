package com.security.otp.core.dtos.otp;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record VerifyOtpReq(
        @JsonProperty("id")
        @NotNull String id,

        @JsonProperty("password")
        @NotNull String password
) {}