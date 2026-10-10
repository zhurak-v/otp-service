package com.security.otp.infrastructure.repositories;

import com.security.otp.core.exceptions.conflict.ConflictException;
import com.security.otp.core.exceptions.not_found.NotFoundException;
import com.security.otp.core.models.entity.Entity;
import com.security.otp.core.repositories.RepositoryWriter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class MemoryRepositoryWriter<ID extends Serializable, E extends Entity<ID>>
        extends MemoryRepository<ID, E>
        implements RepositoryWriter<ID, E> {

    protected MemoryRepositoryWriter(final Class<ID> idClass, final Class<E> entityClass) {
        super(idClass, entityClass);
    }

    @Override
    public final E save(final E entity) {
        final E previous = this.getStorage().putIfAbsent(entity.getId(), entity);
        if (previous != null) {
            throw new ConflictException("Entity already exists");
        }
        return entity;
    }

    @Override
    public final E update(final E entity) {
        final E previous = this.getStorage().replace(entity.getId(), entity);
        if (previous == null) {
            throw new NotFoundException("Entity not found");
        }
        return entity;
    }

    @Override
    public final E delete(final ID id) {
        final E removed = this.getStorage().remove(id);
        if (removed == null) {
            throw new NotFoundException("Entity not found");
        }
        return removed;
    }

    @Override
    public final Collection<E> delete(final Collection<ID> ids) {
        final List<E> entities = new ArrayList<>();
        ids.forEach(id -> entities.add(this.delete(id)));
        return entities;
    }

}
