package com.security.otp.infrastructure.configurations.random;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Configuration
public class SecureRandomConfiguration {

    public SecureRandomConfiguration() {}

    @Bean
    public SecureRandom secureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (final NoSuchAlgorithmException ex) {
            return new SecureRandom();
        }
    }

}
