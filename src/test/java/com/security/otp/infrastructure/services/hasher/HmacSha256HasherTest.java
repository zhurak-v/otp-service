package com.security.otp.infrastructure.services.hasher;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HmacSha256HasherTest {

    private final HmacSha256Hasher hasher = new HmacSha256Hasher("test-secret");

    @Test
    void verifiesCorrectPasswordAfterBase64RoundTrip() {
        final byte[] password = "483920".getBytes(StandardCharsets.UTF_8);

        final String stored = Base64.getEncoder().encodeToString(this.hasher.hash(password));
        final byte[] restored = Base64.getDecoder().decode(stored);

        assertTrue(this.hasher.verify(restored, password));
    }

    @Test
    void rejectsWrongPassword() {
        final byte[] stored = this.hasher.hash("483920".getBytes(StandardCharsets.UTF_8));

        assertFalse(this.hasher.verify(stored, "000000".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void differentSecretsProduceDifferentHashes() {
        final byte[] password = "483920".getBytes(StandardCharsets.UTF_8);

        final byte[] first = this.hasher.hash(password);
        final byte[] second = new HmacSha256Hasher("other-secret").hash(password);

        assertFalse(java.util.Arrays.equals(first, second));
    }

    @Test
    void sameSecretProducesSameHash() {
        final byte[] password = "483920".getBytes(StandardCharsets.UTF_8);

        assertTrue(java.util.Arrays.equals(
                new HmacSha256Hasher("same-secret").hash(password),
                new HmacSha256Hasher("same-secret").hash(password)
        ));
    }

    @Test
    void blankSecretGeneratesIndependentRandomKeys() {
        final byte[] password = "483920".getBytes(StandardCharsets.UTF_8);

        final byte[] first = new HmacSha256Hasher("").hash(password);
        final byte[] second = new HmacSha256Hasher("   ").hash(password);

        assertFalse(java.util.Arrays.equals(first, second));
    }

}