package com.security.otp.infrastructure.services.otp;

import com.security.otp.core.dtos.generator.GenerateCodeReq;
import com.security.otp.core.dtos.otp.CreateOtpReq;
import com.security.otp.core.dtos.otp.CreateOtpRes;
import com.security.otp.core.dtos.otp.VerifyOtpReq;
import com.security.otp.core.dtos.otp.VerifyOtpRes;
import com.security.otp.core.exceptions.conflict.ConflictException;
import com.security.otp.core.exceptions.not_found.NotFoundException;
import com.security.otp.infrastructure.repositories.MemoryOptRepositoryReader;
import com.security.otp.infrastructure.repositories.MemoryOptRepositoryWriter;
import com.security.otp.infrastructure.services.generator.SecureCodeGenerator;
import com.security.otp.infrastructure.services.hasher.HmacSha256Hasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OtpConcurrencyTest {

    private static final int THREADS = 32;

    private OtpServiceImpl service;

    @BeforeEach
    void setUp() {
        this.service = new OtpServiceImpl(
                new MemoryOptRepositoryReader(),
                new MemoryOptRepositoryWriter(),
                new SecureCodeGenerator(new SecureRandom()),
                new HmacSha256Hasher("test-secret"),
                new OtpLocks()
        );
    }

    private static CreateOtpReq request(final String id, final int attempts) {
        return new CreateOtpReq(
                id,
                Duration.ofMinutes(5),
                attempts,
                new GenerateCodeReq(6, Set.of(GenerateCodeReq.CharacterType.DIGITS))
        );
    }

    private static List<Object> runConcurrently(final Supplier<Object> action) throws Exception {
        final ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        final CountDownLatch ready = new CountDownLatch(THREADS);
        final CountDownLatch start = new CountDownLatch(1);
        final List<Future<Object>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < THREADS; i++) {
                futures.add(pool.submit(() -> {
                    ready.countDown();
                    start.await();
                    try {
                        return action.get();
                    } catch (final RuntimeException exception) {
                        return exception;
                    }
                }));
            }

            ready.await();
            start.countDown();

            final List<Object> results = new ArrayList<>();
            for (final Future<Object> future : futures) {
                results.add(future.get(10, TimeUnit.SECONDS));
            }
            return results;
        } finally {
            pool.shutdownNow();
        }
    }

    private static long count(final List<Object> results, final Class<?> type) {
        return results.stream().filter(type::isInstance).count();
    }

    @Test
    void correctCodeVerifiesExactlyOnceUnderParallelRequests() throws Exception {
        final String id = UUID.randomUUID().toString();
        final CreateOtpRes created = this.service.createOtp(request(id, 3));

        final List<Object> results = runConcurrently(
                () -> this.service.verifyOtp(new VerifyOtpReq(id, created.password()))
        );

        assertEquals(1, count(results, VerifyOtpRes.class));
        assertEquals(THREADS - 1, count(results, NotFoundException.class));
    }

    @Test
    void attemptLimitHoldsUnderParallelGuesses() throws Exception {
        final String id = UUID.randomUUID().toString();
        final int attempts = 5;
        this.service.createOtp(request(id, attempts));

        final List<Object> results = runConcurrently(
                () -> this.service.verifyOtp(new VerifyOtpReq(id, "wrong"))
        );

        assertEquals(attempts, count(results, VerifyOtpRes.class));
        assertEquals(THREADS - attempts, count(results, NotFoundException.class));
    }

    @Test
    void sameIdIsCreatedExactlyOnceUnderParallelRequests() throws Exception {
        final String id = UUID.randomUUID().toString();

        final List<Object> results = runConcurrently(() -> this.service.createOtp(request(id, 3)));

        assertEquals(1, count(results, CreateOtpRes.class));
        assertEquals(THREADS - 1, count(results, ConflictException.class));
    }

}