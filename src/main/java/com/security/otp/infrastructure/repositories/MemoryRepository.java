package com.security.otp.infrastructure.repositories;

import com.security.otp.core.models.entity.Entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class MemoryRepository<ID extends Serializable, E extends Entity<ID>>
        extends AbstractRepository<ID, E> {

    private static final class StorageContainer<ID extends Serializable, E> implements Map<ID, E> {

        private final ConcurrentHashMap<ID, E> indices;
        private final ConcurrentHashMap<ID, Long> sequences;
        private final ConcurrentSkipListMap<Long, ID> order;
        private final AtomicLong sequence;

        StorageContainer() {
            this.indices = new ConcurrentHashMap<>();
            this.sequences = new ConcurrentHashMap<>();
            this.order = new ConcurrentSkipListMap<>();
            this.sequence = new AtomicLong(0);
        }

        public NavigableMap<Long, ID> getOrderStorage() {
            return this.order;
        }

        @Override
        public int size() {
            return this.indices.size();
        }

        @Override
        public boolean isEmpty() {
            return this.indices.isEmpty();
        }

        @Override
        public boolean containsKey(final Object key) {
            return this.indices.containsKey(key);
        }

        @Override
        public boolean containsValue(final Object value) {
            return this.indices.containsValue(value);
        }

        @Override
        public E get(final Object key) {
            return this.indices.get(key);
        }

        @Override
        public E put(final ID key, final E value) {
            final E previous = this.indices.put(key, value);
            if (previous == null) {
                this.sequences.computeIfAbsent(key, this::register);
                if (!this.indices.containsKey(key)) {
                    this.unregister(key);
                }
            }
            return previous;
        }

        private Long register(final ID key) {
            final long next = this.sequence.incrementAndGet();
            this.order.put(next, key);
            return next;
        }

        private void unregister(final ID key) {
            final Long seq = this.sequences.remove(key);
            if (seq != null) {
                this.order.remove(seq);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public E remove(final Object key) {
            final ID id = (ID) key;
            this.unregister(id);
            return this.indices.remove(id);
        }

        @Override
        public void putAll(final Map<? extends ID, ? extends E> m) {
            for (final Map.Entry<? extends ID, ? extends E> entry : m.entrySet()) {
                this.put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void clear() {
            this.indices.clear();
            this.sequences.clear();
            this.order.clear();
        }

        @Override
        public Set<ID> keySet() {
            return this.indices.keySet();
        }

        @Override
        public Collection<E> values() {
            return this.indices.values();
        }

        @Override
        public Set<Entry<ID, E>> entrySet() {
            return this.indices.entrySet();
        }
    }

    private static final class StorageProvider {

        final Map<Class<?>, StorageContainer<?, ?>> storages;

        StorageProvider() {
            this.storages = new ConcurrentHashMap<>();
        }

        @SuppressWarnings("unchecked")
        <K extends Serializable, V> StorageContainer<K, V> getContainer(final Class<?> entityClass) {
            return (StorageContainer<K, V>) this.storages.computeIfAbsent(
                    entityClass,
                    k -> new StorageContainer<>()
            );
        }
    }

    private static final class StorageHolder {
        static final StorageProvider PROVIDER = new StorageProvider();
    }

    private final StorageContainer<ID, E> container;

    protected MemoryRepository(final Class<ID> idClass, final Class<E> entityClass) {
        super(idClass, entityClass);
        this.container = StorageHolder.PROVIDER.getContainer(entityClass);
    }

    protected final Map<ID, E> getStorage() {
        return this.container;
    }

    protected final NavigableMap<Long, ID> getOrderStorage() {
        return this.container.getOrderStorage();
    }
}