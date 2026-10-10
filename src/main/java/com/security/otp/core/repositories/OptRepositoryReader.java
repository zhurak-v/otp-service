package com.security.otp.core.repositories;

import com.security.otp.core.models.entity.OtpEntity;
import com.security.otp.core.models.sort.EntitySort;
import com.security.otp.core.pageable.Pageable;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

public interface OptRepositoryReader extends RepositoryReader<String, OtpEntity> {

    @NotNull
    Collection<OtpEntity> findExpired(
            @NotNull final Pageable pageable,
            @Nullable final EntitySort<String, ? extends OtpEntity> sort
    );

}
