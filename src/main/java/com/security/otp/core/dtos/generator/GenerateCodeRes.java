package com.security.otp.core.dtos.generator;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record GenerateCodeRes(
        @JsonProperty("code")
        @NotNull String code
) {}