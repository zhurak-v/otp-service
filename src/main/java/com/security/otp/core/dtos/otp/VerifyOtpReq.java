package com.security.otp.core.dtos.otp;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyOtpReq(
        @JsonProperty("id")
        @NotBlank @Size(max = 128) String id,

        @JsonProperty("password")
        @NotBlank @Size(max = 128) String password
) {}
