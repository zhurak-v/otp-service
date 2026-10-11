package com.security.otp.core.dtos.otp;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateOtpRes(
        @JsonProperty("password") String password
) {

        @Override
        public String toString() {
                return "CreateOtpRes[password=***]";
        }

}