package com.security.otp.infrastructure.services.generator;

import com.security.otp.core.dtos.generator.GenerateCodeReq;
import com.security.otp.core.dtos.generator.GenerateCodeRes;
import com.security.otp.core.services.generator.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Set;

@Service
public final class SecureCodeGenerator implements CodeGenerator {

    private static final String DIGITS_POOL = "0123456789";
    private static final String LETTERS_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final SecureRandom secureRandom;

    @Autowired
    public SecureCodeGenerator(final SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    @Override
    public GenerateCodeRes generate(final GenerateCodeReq request) {
        final String pool = buildCharacterPool(request.characterTypes());
        final String code = generateRandomCode(pool, request.length());

        return new GenerateCodeRes(code);
    }

    private String buildCharacterPool(final Set<GenerateCodeReq.CharacterType> types) {
        final StringBuilder pool = new StringBuilder();

        if (types.contains(GenerateCodeReq.CharacterType.DIGITS)) {
            pool.append(DIGITS_POOL);
        }
        if (types.contains(GenerateCodeReq.CharacterType.LETTERS)) {
            pool.append(LETTERS_POOL);
        }

        if (pool.isEmpty()) {
            throw new IllegalArgumentException("At least one character type must be selected");
        }

        return pool.toString();
    }

    private String generateRandomCode(final String pool, final int length) {
        final StringBuilder code = new StringBuilder(length);
        final int poolLength = pool.length();

        for (int i = 0; i < length; i++) {
            final int index = this.secureRandom.nextInt(poolLength);
            code.append(pool.charAt(index));
        }

        return code.toString();
    }
}
