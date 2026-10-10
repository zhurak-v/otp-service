package com.security.otp.core.repositories;

import com.security.otp.core.models.entity.Entity;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public interface Repository<ID extends Serializable, E extends Entity<ID>> {

    @NotNull
    Class<ID> getIdClass();

    @NotNull
    Class<E> getEntityClass();

}
