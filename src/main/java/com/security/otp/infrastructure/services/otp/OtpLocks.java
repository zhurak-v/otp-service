package com.security.otp.infrastructure.services.otp;

import org.springframework.stereotype.Component;

@Component
public final class OtpLocks {

    private static final int STRIPES = 64;

    private final Object[] locks = new Object[STRIPES];

    public OtpLocks() {
        for (int i = 0; i < STRIPES; i++) {
            this.locks[i] = new Object();
        }
    }

    public Object of(final String id) {
        return this.locks[Math.floorMod(id.hashCode(), STRIPES)];
    }

}
