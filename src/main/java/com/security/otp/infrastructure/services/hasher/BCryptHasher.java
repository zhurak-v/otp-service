package com.security.otp.infrastructure.services.hasher;

import com.security.otp.core.services.hasher.VerifiableHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public final class BCryptHasher implements VerifiableHasher {

    private final BCryptPasswordEncoder encoder;

    public BCryptHasher() {
        this.encoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public byte[] hash(final byte[] value) {
        return this.encoder.encode(Base64.getEncoder().encodeToString(value)
        ).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean verify(final byte[] hash, final byte[] value) {
        if (hash == null || value == null) {
            return false;
        }

        return this.encoder.matches(
                Base64.getEncoder().encodeToString(value),
                new String(hash, StandardCharsets.UTF_8)
        );
    }
}