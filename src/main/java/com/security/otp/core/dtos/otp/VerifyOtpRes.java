package com.security.otp.core.dtos.otp;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VerifyOtpRes(
        @JsonProperty("verified") boolean verified,
        @JsonProperty("attempts") int attempts
) {}