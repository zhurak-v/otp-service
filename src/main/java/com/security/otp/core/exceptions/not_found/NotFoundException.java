package com.security.otp.core.exceptions.not_found;

import com.security.otp.core.exceptions.AppException;

public final class NotFoundException extends AppException {

    private static final String CODE = "NOT_FOUND";

    public NotFoundException(final String message) {
        super(CODE, message);
    }

}
