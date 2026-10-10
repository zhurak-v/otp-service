package com.security.otp.core.services.hasher;

import jakarta.validation.constraints.NotNull;

public interface Hasher {
    @NotNull
    byte[] hash(@NotNull final byte[] value);
}
