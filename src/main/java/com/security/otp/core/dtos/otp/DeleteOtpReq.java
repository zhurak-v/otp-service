package com.security.otp.core.dtos.otp;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteOtpReq(
        @JsonProperty("id") String id
) {}