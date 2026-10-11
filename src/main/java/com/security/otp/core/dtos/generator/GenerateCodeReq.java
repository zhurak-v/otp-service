package com.security.otp.core.dtos.generator;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record GenerateCodeReq(
        @JsonProperty("length")
        @NotNull @Min(1) @Max(32) Integer length,

        @JsonProperty("characterTypes")
        @NotEmpty Set<@NotNull CharacterType> characterTypes
) {

    public enum CharacterType {
        DIGITS,
        LETTERS
    }

}