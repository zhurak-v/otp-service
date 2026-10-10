package com.security.otp.core.exceptions.expired;

import com.security.otp.core.exceptions.AppException;

public final class ExpiredOtpException extends AppException {

    private static final String CODE = "OTP_EXPIRED";

    public ExpiredOtpException(final String message) {
        super(CODE, message);
    }

}
