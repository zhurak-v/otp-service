package com.security.otp.core.models.expirable;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public interface Expirable {

    @NotNull
    Instant getExpiresAt();

    default boolean isExpired() {
        final Instant now = Instant.now();
        return now.isAfter(this.getExpiresAt());
    }

}
