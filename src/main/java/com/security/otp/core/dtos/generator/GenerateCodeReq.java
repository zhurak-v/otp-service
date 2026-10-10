package com.security.otp.core.dtos.generator;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record GenerateCodeReq(
        @JsonProperty("length")
        @Min(1) @Max(32) int length,

        @JsonProperty("characterTypes")
        @NotNull @NotEmpty Set<CharacterType> characterTypes
) {
    public enum CharacterType {
        DIGITS,
        LETTERS
    }
}
