package com.security.otp.core.models.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

public abstract class AbstractEntity<ID extends Serializable> implements Entity<ID> {

    private final ID id;
    private final Instant createdAt;
    private Instant updatedAt;

    protected AbstractEntity(
            final ID id,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected AbstractEntity(final ID id) {
        this(id, Instant.now(), null);
    }

    @Override
    public final ID getId() {
        return this.id;
    }

    @Override
    public final Instant getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public final Optional<Instant> getUpdatedAt() {
        return Optional.ofNullable(this.updatedAt);
    }

    @Override
    public final Instant setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
        return updatedAt;
    }

}
