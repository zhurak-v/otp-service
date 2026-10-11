package com.security.otp.infrastructure.services.hasher;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BCryptHasherTest {

    private final BCryptHasher hasher = new BCryptHasher();

    @Test
    void verifiesCorrectPasswordAfterBase64RoundTrip() {
        final byte[] password = "483920".getBytes(StandardCharsets.UTF_8);

        final String stored = Base64.getEncoder().encodeToString(
                this.hasher.hash(password)
        );
        final byte[] restored = Base64.getDecoder().decode(stored);

        assertTrue(this.hasher.verify(restored, password));
    }

    @Test
    void rejectsWrongPassword() {
        final byte[] stored = this.hasher.hash(
                "483920".getBytes(StandardCharsets.UTF_8)
        );

        assertFalse(this.hasher.verify(
                stored,
                "000000".getBytes(StandardCharsets.UTF_8)
        ));
    }

    @Test
    void samePasswordProducesDifferentHashes() {
        final byte[] password = "483920".getBytes(StandardCharsets.UTF_8);

        final byte[] first = this.hasher.hash(password);
        final byte[] second = this.hasher.hash(password);

        assertNotEquals(
                new String(first, StandardCharsets.UTF_8),
                new String(second, StandardCharsets.UTF_8)
        );
    }

    @Test
    void rejectsNullHash() {
        assertFalse(this.hasher.verify(
                null,
                "483920".getBytes(StandardCharsets.UTF_8)
        ));
    }

    @Test
    void rejectsNullPassword() {
        final byte[] hash = this.hasher.hash(
                "483920".getBytes(StandardCharsets.UTF_8)
        );

        assertFalse(this.hasher.verify(hash, null));
    }
}