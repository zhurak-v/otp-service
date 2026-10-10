package com.security.otp.core.dtos.generator;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record GenerateCodeReq(
        @JsonProperty("length")
        @Min(1) int length,

        @JsonProperty("characterTypes")
        @NotEmpty Set<CharacterType> characterTypes
) {
    public enum CharacterType {
        DIGITS,
        LETTERS
    }
}