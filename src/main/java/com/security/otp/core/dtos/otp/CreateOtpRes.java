package com.security.otp.core.dtos.otp;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record CreateOtpRes(
        @JsonProperty("password")
        @NotNull String password
) {}