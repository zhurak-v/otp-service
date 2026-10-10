package com.security.otp.core.exceptions.internal;

import com.security.otp.core.exceptions.AppException;

public final class InternalException extends AppException {

    public static final String CODE = "INTERNAL_ERROR";
    public static final String MESSAGE = "Internal Error";

    public InternalException() {
        super(CODE, MESSAGE);
    }

}
