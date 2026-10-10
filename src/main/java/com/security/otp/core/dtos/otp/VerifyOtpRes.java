package com.security.otp.core.dtos.otp;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.PositiveOrZero;

public record VerifyOtpRes(
        @JsonProperty("verified")
        boolean verified,

        @JsonProperty("attempts")
        @PositiveOrZero int attempts
) {}