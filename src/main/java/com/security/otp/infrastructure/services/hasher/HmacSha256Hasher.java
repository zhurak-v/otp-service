package com.security.otp.infrastructure.services.hasher;

import com.security.otp.core.services.hasher.VerifiableHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

@Service
public final class HmacSha256Hasher implements VerifiableHasher {

    private static final String ALGORITHM = "HmacSHA256";
    private static final int GENERATED_SECRET_BYTES = 32;

    private final SecretKeySpec key;

    public HmacSha256Hasher(@Value("${otp.hash-secret:}") final String secret) {
        final byte[] keyBytes;

        if (secret == null || secret.isBlank()) {
            keyBytes = new byte[GENERATED_SECRET_BYTES];
            new SecureRandom().nextBytes(keyBytes);
        } else {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        this.key = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    @Override
    public byte[] hash(final byte[] value) {
        try {
            final Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(this.key);
            return mac.doFinal(value);
        } catch (final GeneralSecurityException exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Override
    public boolean verify(final byte[] hash, final byte[] value) {
        return MessageDigest.isEqual(hash, this.hash(value));
    }

}
