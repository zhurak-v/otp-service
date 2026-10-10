package com.security.otp.core.repositories;

import com.security.otp.core.models.entity.Entity;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Collection;

public interface RepositoryWriter<ID extends Serializable, E extends Entity<ID>>
        extends Repository<ID, E> {

    @NotNull
    E save(@NotNull final E entity);

    @NotNull
    E update(@NotNull final E entity);

    @NotNull
    E delete(@NotNull final ID id);

    @NotNull
    Collection<E> delete(@NotNull final Collection<ID> ids);

}