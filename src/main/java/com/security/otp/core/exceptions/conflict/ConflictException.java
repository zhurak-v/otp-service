package com.security.otp.core.exceptions.conflict;

import com.security.otp.core.exceptions.AppException;

public final class ConflictException extends AppException {

    private static final String CODE = "CONFLICT";

    public ConflictException(final String message) {
        super(CODE, message);
    }

}
