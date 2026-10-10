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
import java.util.Base64;

@Service
public final class OtpServiceImpl implements OtpService {

    private final OptRepositoryReader optRepositoryReader;
    private final OptRepositoryWriter optRepositoryWriter;
    private final CodeGenerator codeGenerator;
    private final VerifiableHasher verifiableHasher;
    private final OtpLocks otpLocks;

    @Autowired
    public OtpServiceImpl(
            final OptRepositoryReader optRepositoryReader,
            final OptRepositoryWriter optRepositoryWriter,
            final CodeGenerator codeGenerator,
            final VerifiableHasher verifiableHasher,
            final OtpLocks otpLocks
    ) {
        this.optRepositoryReader = optRepositoryReader;
        this.optRepositoryWriter = optRepositoryWriter;
        this.codeGenerator = codeGenerator;
        this.verifiableHasher = verifiableHasher;
        this.otpLocks = otpLocks;
    }

    @Override
    public CreateOtpRes createOtp(final CreateOtpReq dto) {
        final String id = dto.id();

        synchronized (this.otpLocks.of(id)) {
            if (this.optRepositoryReader.existsById(id)) {
                throw new ConflictException("OTP already exists");
            }

            final String password = this.codeGenerator.generate(dto.generateCodeReq()).code();

            this.optRepositoryWriter.save(new OtpEntity(
                    id,
                    dto.lifetime(),
                    this.hashPassword(password),
                    dto.attempts()
            ));

            return new CreateOtpRes(password);
        }
    }

    @Override
    public void deleteOtp(final DeleteOtpReq dto) {
        final String id = dto.id();

        synchronized (this.otpLocks.of(id)) {
            if (!this.optRepositoryReader.existsById(id)) {
                throw new NotFoundException("OTP not found");
            }

            this.optRepositoryWriter.delete(id);
        }
    }

    @Override
    public VerifyOtpRes verifyOtp(final VerifyOtpReq dto) {
        final String id = dto.id();

        synchronized (this.otpLocks.of(id)) {
            final OtpEntity entity = this.optRepositoryReader.findById(id)
                    .orElseThrow(() -> new NotFoundException("OTP not found"));

            if (entity.isExpired()) {
                this.optRepositoryWriter.delete(id);
                throw new ExpiredOtpException("OTP is expired");
            }

            final boolean matches = this.matches(entity.getHashedPassword(), dto.password());

            if (matches) {
                this.optRepositoryWriter.delete(id);
                return new VerifyOtpRes(true, entity.getAttempts());
            }

            if (entity.getAttempts() <= 1) {
                this.optRepositoryWriter.delete(id);
                return new VerifyOtpRes(false, 0);
            }

            entity.decreaseAttempts();
            this.optRepositoryWriter.update(entity);
            return new VerifyOtpRes(false, entity.getAttempts());
        }
    }

    private String hashPassword(final String password) {
        final byte[] hash = this.verifiableHasher.hash(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    private boolean matches(final String storedHash, final String password) {
        final byte[] hash = Base64.getDecoder().decode(storedHash);
        return this.verifiableHasher.verify(hash, password.getBytes(StandardCharsets.UTF_8));
    }

}
