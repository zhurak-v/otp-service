package com.security.otp.infrastructure.repositories;

import com.security.otp.core.exceptions.conflict.ConflictException;
import com.security.otp.core.exceptions.not_found.NotFoundException;
import com.security.otp.core.models.entity.Entity;
import com.security.otp.core.repositories.RepositoryWriter;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class MemoryRepositoryWriter<ID extends Serializable, E extends Entity<ID>>
        extends MemoryRepository<ID, E>
        implements RepositoryWriter<ID, E> {

    protected MemoryRepositoryWriter(final Class<ID> idClass, final Class<E> entityClass) {
        super(idClass, entityClass);
    }

    @Override
    public final E save(final E entity) {
        final ID id = entity.getId();
        final Map<ID, E> storage = this.getStorage();
        if (storage.containsKey(id)) {
            throw new ConflictException("Entity already exists");
        }
        storage.put(id, entity);
        return entity;
    }

    @Override
    public final E update(final E entity) {
        final ID id = entity.getId();
        final Map<ID, E> storage = this.getStorage();
        if (!storage.containsKey(id)) {
            throw new NotFoundException("Entity not found");
        }
        storage.put(id, entity);
        return entity;
    }

    @Override
    public final E delete(final ID id) {
        final Map<ID, E> storage = this.getStorage();
        if (!storage.containsKey(id)) {
            throw new NotFoundException("Entity not found");
        }
        return storage.remove(id);
    }

    @Override
    public final Collection<E> delete(final Collection<ID> ids) {
        final List<E> entities = new ArrayList<>();
        ids.forEach(id -> entities.add(this.delete(id)));
        return entities;
    }

}
