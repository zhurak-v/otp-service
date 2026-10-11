package com.security.otp.core.dtos.otp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.security.otp.core.dtos.generator.GenerateCodeReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Duration;

public record CreateOtpReq(
        @JsonProperty("id")
        @NotBlank @Size(max = 128) String id,

        @JsonProperty("lifetime")
        @NotNull Duration lifetime,

        @JsonProperty("attempts")
        @NotNull @Min(1) @Max(10) Integer attempts,

        @JsonProperty("generateCodeReq")
        @NotNull @Valid GenerateCodeReq generateCodeReq
) {

        private static final Duration MIN_LIFETIME = Duration.ofSeconds(1);
        private static final Duration MAX_LIFETIME = Duration.ofDays(1);

        @JsonIgnore
        @AssertTrue(message = "lifetime must be between 1 second and 24 hours")
        public boolean isLifetimeInRange() {
                return this.lifetime == null
                        || (this.lifetime.compareTo(MIN_LIFETIME) >= 0
                        && this.lifetime.compareTo(MAX_LIFETIME) <= 0);
        }

}