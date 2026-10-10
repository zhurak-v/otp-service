package com.security.otp.adapters.http.handlers;

import com.security.otp.core.dtos.otp.*;
import com.security.otp.core.services.otp.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public final class OtpHandler {

    private final OtpService otpService;

    @Autowired
    public OtpHandler(final OtpService otpService) {
        this.otpService = otpService;
    }

    public ResponseEntity<CreateOtpRes> createOtp(final CreateOtpReq dto) {
        final CreateOtpRes response = this.otpService.createOtp(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<Void> deleteOtp(final DeleteOtpReq dto) {
        this.otpService.deleteOtp(dto);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<VerifyOtpRes> verifyOtp(final VerifyOtpReq dto) {
        final VerifyOtpRes response = this.otpService.verifyOtp(dto);
        return ResponseEntity.ok(response);
    }

}
