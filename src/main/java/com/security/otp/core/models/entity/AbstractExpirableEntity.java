package com.security.otp.core.models.entity;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

public abstract class AbstractExpirableEntity<ID extends Serializable>
        extends AbstractEntity<ID>
        implements ExpirableEntity<ID> {

    private final Duration lifetime;

    protected AbstractExpirableEntity(
            final ID id,
            final Instant createdAt,
            final Instant updatedAt,
            final Duration lifetime
    ) {
        super(id, createdAt, updatedAt);
        this.lifetime = lifetime;
    }

    protected AbstractExpirableEntity(
            final ID id,
            final Duration lifetime
    ) {
        super(id);
        this.lifetime = lifetime;
    }

    public final Duration getLifetime() {
        return this.lifetime;
    }

    @Override
    public final Instant getExpiresAt() {
        return this.getCreatedAt().plus(this.lifetime);
    }

}
