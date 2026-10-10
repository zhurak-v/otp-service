package com.security.otp.core.exceptions;

public abstract class AppException extends RuntimeException {

    private static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";
    private static final String INTERNAL_ERROR_MESSAGE = "Internal Error";

    private final String code;

    protected AppException(final String code, final String message) {
        super(message);
        this.code = code;
    }

    public final String getCode() {
        return this.code;
    }

}
