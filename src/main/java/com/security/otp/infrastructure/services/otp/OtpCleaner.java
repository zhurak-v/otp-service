package com.security.otp.infrastructure.services.otp;

import com.security.otp.core.models.entity.OtpEntity;
import com.security.otp.core.pageable.Page;
import com.security.otp.core.repositories.OptRepositoryReader;
import com.security.otp.core.repositories.OptRepositoryWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class OtpCleaner {

    private static final long FIXED_RATE = 10_000L;

    private final OptRepositoryReader optRepositoryReader;
    private final OptRepositoryWriter optRepositoryWriter;

    @Autowired
    public OtpCleaner(
            final OptRepositoryReader optRepositoryReader,
            final OptRepositoryWriter optRepositoryWriter
    ) {
        this.optRepositoryReader = optRepositoryReader;
        this.optRepositoryWriter = optRepositoryWriter;
    }

    @Scheduled(fixedRate = FIXED_RATE)
    public void clean() {
        final Collection<OtpEntity> entities = this.optRepositoryReader.findExpired(
                new Page(0, 10),
                null
        );

        final Set<String> ids = entities.stream()
                .map(OtpEntity::getId)
                .collect(Collectors.toSet());

        this.optRepositoryWriter.delete(ids);
    }

}
