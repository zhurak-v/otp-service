package com.security.otp.adapters.http.controllers;

import com.security.otp.adapters.http.handlers.OtpHandler;
import com.security.otp.core.dtos.otp.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping(value = "/otp")
public class OtpController {

    private final OtpHandler otpHandler;

    @Autowired
    public OtpController(final OtpHandler otpHandler) {
        this.otpHandler = otpHandler;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<CreateOtpRes> createOtp(
            @Valid @RequestBody final CreateOtpReq dto
    ) {
        return this.otpHandler.createOtp(dto);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteOtp(
            @Valid @PathVariable("id") final String id
    ) {
        return this.otpHandler.deleteOtp(new DeleteOtpReq(id));
    }

    @PostMapping(value = "/verify")
    public ResponseEntity<VerifyOtpRes> verifyOtp(
            @Valid @RequestBody final VerifyOtpReq dto
    ) {
        return this.otpHandler.verifyOtp(dto);
    }

}
