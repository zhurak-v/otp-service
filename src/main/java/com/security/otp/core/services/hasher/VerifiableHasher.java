package com.security.otp.core.services.hasher;

import jakarta.validation.constraints.NotNull;

public interface VerifiableHasher extends Hasher {
    boolean verify(@NotNull final byte[] hash, @NotNull final byte[] value);
}
