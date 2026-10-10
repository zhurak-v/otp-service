package com.security.otp.core.models.entity;

import java.time.Duration;
import java.time.Instant;

public final class OtpEntity extends AbstractExpirableEntity<String> {

    private final String hashedPassword;
    private int attempts;

    public OtpEntity(
            final String id,
            final Instant createdAt,
            final Instant updatedAt,
            final Duration lifetime,
            final String hashedPassword,
            final int attempts
    ) {
        super(id, createdAt, updatedAt, lifetime);
        this.hashedPassword = hashedPassword;
        this.attempts = attempts;
    }

    public OtpEntity(
            final String id,
            final Duration lifetime,
            final String hashedPassword,
            final int attempts
    ) {
        super(id, lifetime);
        this.hashedPassword = hashedPassword;
        this.attempts = attempts;
    }

    public String getHashedPassword() {
        return this.hashedPassword;
    }

    public int getAttempts() {
        return this.attempts;
    }

    public OtpEntity decreaseAttempts() {
        if (this.attempts > 0) {
            this.attempts--;
        }
        return this;
    }

}
