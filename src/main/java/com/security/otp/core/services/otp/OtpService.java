package com.security.otp.core.services.otp;

import com.security.otp.core.dtos.otp.*;
import jakarta.validation.constraints.NotNull;

public interface OtpService {
    @NotNull
    CreateOtpRes createOtp(@NotNull final CreateOtpReq dto);

    void deleteOtp(@NotNull final DeleteOtpReq dto);

    @NotNull
    VerifyOtpRes verifyOtp(@NotNull final VerifyOtpReq dto);
}
