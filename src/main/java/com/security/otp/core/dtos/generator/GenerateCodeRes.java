package com.security.otp.core.dtos.generator;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GenerateCodeRes(
        @JsonProperty("code") String code
) {

        @Override
        public String toString() {
                return "GenerateCodeRes[code=***]";
        }

}