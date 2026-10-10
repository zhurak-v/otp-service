package com.security.otp.infrastructure.repositories;

import com.security.otp.core.models.entity.Entity;
import com.security.otp.core.repositories.Repository;

import java.io.Serializable;

public abstract class AbstractRepository<ID extends Serializable, E extends Entity<ID>>
        implements Repository<ID, E> {

    private final Class<ID> idClass;
    private final Class<E> entityClass;

    protected AbstractRepository(final Class<ID> idClass, final Class<E> entityClass) {
        this.idClass = idClass;
        this.entityClass = entityClass;
    }

    @Override
    public final Class<ID> getIdClass() {
        return this.idClass;
    }

    @Override
    public final Class<E> getEntityClass() {
        return this.entityClass;
    }

}
