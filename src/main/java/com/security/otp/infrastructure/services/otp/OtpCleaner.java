package com.security.otp.infrastructure.services.otp;

import com.security.otp.core.models.entity.OtpEntity;
import com.security.otp.core.pageable.Page;
import com.security.otp.core.repositories.OptRepositoryReader;
import com.security.otp.core.repositories.OptRepositoryWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public final class OtpCleaner {

    private static final long FIXED_DELAY = 10_000L;
    private static final int BATCH_SIZE = 500;
    private static final int MAX_BATCHES_PER_RUN = 20;

    private final OptRepositoryReader optRepositoryReader;
    private final OptRepositoryWriter optRepositoryWriter;
    private final OtpLocks otpLocks;

    @Autowired
    public OtpCleaner(
            final OptRepositoryReader optRepositoryReader,
            final OptRepositoryWriter optRepositoryWriter,
            final OtpLocks otpLocks
    ) {
        this.optRepositoryReader = optRepositoryReader;
        this.optRepositoryWriter = optRepositoryWriter;
        this.otpLocks = otpLocks;
    }

    @Scheduled(fixedDelay = FIXED_DELAY)
    public void clean() {
        for (int batch = 0; batch < MAX_BATCHES_PER_RUN; batch++) {
            final Collection<OtpEntity> entities = this.optRepositoryReader.findExpired(
                    new Page(0, BATCH_SIZE),
                    null
            );

            if (entities.isEmpty()) {
                return;
            }

            for (final OtpEntity entity : entities) {
                this.deleteIfStillExpired(entity.getId());
            }

            if (entities.size() < BATCH_SIZE) {
                return;
            }
        }
    }

    private void deleteIfStillExpired(final String id) {
        synchronized (this.otpLocks.of(id)) {
            final boolean expired = this.optRepositoryReader.findById(id)
                    .map(OtpEntity::isExpired)
                    .orElse(false);

            if (expired) {
                this.optRepositoryWriter.delete(id);
            }
        }
    }

}
