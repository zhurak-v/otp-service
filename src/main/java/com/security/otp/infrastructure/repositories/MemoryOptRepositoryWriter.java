package com.security.otp.infrastructure.repositories;

import com.security.otp.core.models.entity.OtpEntity;
import com.security.otp.core.repositories.OptRepositoryWriter;
import org.springframework.stereotype.Service;

@Service
public final class MemoryOptRepositoryWriter
        extends MemoryRepositoryWriter<String, OtpEntity>
        implements OptRepositoryWriter {

    public MemoryOptRepositoryWriter() {
        super(String.class, OtpEntity.class);
    }

}
