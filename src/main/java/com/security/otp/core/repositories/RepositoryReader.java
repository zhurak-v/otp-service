package com.security.otp.core.repositories;

import com.security.otp.core.models.entity.Entity;
import com.security.otp.core.models.sort.EntitySort;
import com.security.otp.core.pageable.Pageable;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public interface RepositoryReader<ID extends Serializable, E extends Entity<ID>>
        extends Repository<ID, E> {

    @NotNull
    Optional<E> findById(@NotNull final ID id);

    boolean existsById(@NotNull final ID id);

    @NotNull
    Collection<E> findAll(
            @NotNull final Pageable pageable,
            @NotNull final EntitySort<? super ID, ? extends E> sort
    );

    long count();

}
