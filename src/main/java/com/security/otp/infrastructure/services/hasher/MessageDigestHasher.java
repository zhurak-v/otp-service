package com.security.otp.infrastructure.services.hasher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.security.otp.core.services.hasher.VerifiableHasher;
import org.springframework.stereotype.Service;

@Service
public final class MessageDigestHasher implements VerifiableHasher {

    private static final String ALGORITHM = "SHA-256";

    public MessageDigestHasher() {}

    @Override
    public byte[] hash(final byte[] value) {
        try {
            return MessageDigest.getInstance(ALGORITHM).digest(value);
        } catch (final NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Override
    public boolean verify(final byte[] hash, final byte[] value) {
        return MessageDigest.isEqual(hash, this.hash(value));
    }

}
