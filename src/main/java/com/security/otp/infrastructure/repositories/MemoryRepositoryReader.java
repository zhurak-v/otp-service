package com.security.otp.infrastructure.repositories;

import com.security.otp.core.models.entity.Entity;
import com.security.otp.core.models.sort.EntitySort;
import com.security.otp.core.pageable.Pageable;
import com.security.otp.core.repositories.RepositoryReader;

import java.io.Serializable;
import java.util.*;

public abstract class MemoryRepositoryReader<ID extends Serializable, E extends Entity<ID>>
        extends MemoryRepository<ID, E>
        implements RepositoryReader<ID, E> {

    protected MemoryRepositoryReader(final Class<ID> idClass, final Class<E> entityClass) {
        super(idClass, entityClass);
    }

    @Override
    public final Optional<E> findById(final ID id) {
        final E entity = this.getStorage().get(id);
        return Optional.ofNullable(entity);
    }

    @Override
    public final boolean existsById(final ID id) {
        return this.getStorage().containsKey(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Collection<E> findAll(
            final Pageable pageable,
            final EntitySort<? super ID, ? extends E> sort
    ) {
        final long offset = pageable.getOffset();
        final int limit = pageable.getLimit();
        final Map<ID, E> storage = this.getStorage();
        final Collection<ID> ids = this.getOrderStorage().values();

        final Comparator<E> comparator = (Comparator<E>) sort;
        final long size = offset + limit;
        final PriorityQueue<E> heap = new PriorityQueue<>(
                (int) Math.min(size, 1024L),
                comparator.reversed()
        );

        for (final ID id : ids) {
            final E entity = storage.get(id);
            if (entity == null) {
                continue;
            }
            if (heap.size() < size) {
                heap.add(entity);
            } else if (comparator.compare(entity, heap.peek()) < 0) {
                heap.poll();
                heap.add(entity);
            }
        }

        final int count = heap.size();
        if (offset >= count) {
            return List.of();
        }

        final List<E> sorted = new ArrayList<>(count);
        while (!heap.isEmpty()) {
            sorted.add(heap.poll());
        }
        Collections.reverse(sorted);
        return List.copyOf(sorted.subList(
                (int) offset,
                Math.min((int) offset + limit, count)
        ));
    }

    @Override
    public final long count() {
        return this.getStorage().size();
    }

}
