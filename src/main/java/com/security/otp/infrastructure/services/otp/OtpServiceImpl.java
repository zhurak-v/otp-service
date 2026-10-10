package com.security.otp.infrastructure.services.otp;

import com.security.otp.core.dtos.otp.*;
import com.security.otp.core.exceptions.conflict.ConflictException;
import com.security.otp.core.exceptions.expired.ExpiredOtpException;
import com.security.otp.core.exceptions.not_found.NotFoundException;
import com.security.otp.core.models.entity.OtpEntity;
import com.security.otp.core.repositories.OptRepositoryReader;
import com.security.otp.core.repositories.OptRepositoryWriter;
import com.security.otp.core.services.generator.CodeGenerator;
import com.security.otp.core.services.hasher.VerifiableHasher;
import com.security.otp.core.services.otp.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public final class OtpServiceImpl implements OtpService {

    private final OptRepositoryReader optRepositoryReader;
    private final OptRepositoryWriter optRepositoryWriter;
    private final CodeGenerator codeGenerator;
    private final VerifiableHasher verifiableHasher;

    @Autowired
    public OtpServiceImpl(
            final OptRepositoryReader optRepositoryReader,
            final OptRepositoryWriter optRepositoryWriter,
            final CodeGenerator codeGenerator,
            final VerifiableHasher verifiableHasher
    ) {
        this.optRepositoryReader = optRepositoryReader;
        this.optRepositoryWriter = optRepositoryWriter;
        this.codeGenerator = codeGenerator;
        this.verifiableHasher = verifiableHasher;
    }

    @Override
    public CreateOtpRes createOtp(final CreateOtpReq dto) {
        final String id = dto.id();

        if (this.optRepositoryReader.existsById(id)) {
            throw new ConflictException("OTP already exists");
        }

        final String password = this.codeGenerator.generate(dto.generateCodeReq()).code();
        final byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

        final String hashedPassword = new String(
                this.verifiableHasher.hash(passwordBytes),
                StandardCharsets.UTF_8
        );

        final OtpEntity entity = this.optRepositoryWriter.save(new OtpEntity(
                id,
                dto.lifetime(),
                hashedPassword,
                dto.attempts()
        ));

        return new CreateOtpRes(password);
    }

    @Override
    public void deleteOtp(final DeleteOtpReq dto) {
        final String id = dto.id();

        if (!this.optRepositoryReader.existsById(id)) {
            throw new NotFoundException("OTP not found");
        }

        this.optRepositoryWriter.delete(id);
    }

    @Override
    public VerifyOtpRes verifyOtp(final VerifyOtpReq dto) {
        final String id = dto.id();

        if (!this.optRepositoryReader.existsById(id)) {
            throw new NotFoundException("OTP not found");
        }

        final OtpEntity entity = this.optRepositoryReader.findById(id)
                .orElseThrow(() -> new NotFoundException("OTP not found"));

        if (entity.isExpired()) {
            throw new ExpiredOtpException("OTP is expired");
        }

        final byte[] hashedPasswordBytes = entity.getHashedPassword().getBytes(StandardCharsets.UTF_8);
        final byte[] passwordBytes = dto.password().getBytes(StandardCharsets.UTF_8);

        final boolean matches = this.verifiableHasher.verify(hashedPasswordBytes, passwordBytes);

        if (!matches) {
            if (entity.getAttempts() <= 1) {
                this.optRepositoryWriter.delete(id);
                return new VerifyOtpRes(false, 0);
            }
            entity.decreaseAttempts();
            this.optRepositoryWriter.update(entity);
            return new VerifyOtpRes(false, entity.getAttempts());
        }

        this.optRepositoryWriter.delete(id);

        return new VerifyOtpRes(true, entity.getAttempts());
    }

}
