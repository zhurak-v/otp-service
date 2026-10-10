package com.security.otp.core.models.auditable;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Optional;

public interface Auditable {

    @NotNull
    Instant getCreatedAt();

    @NotNull
    Optional<Instant> getUpdatedAt();

    @NotNull
    Instant setUpdatedAt(@NotNull final Instant updatedAt);

}
