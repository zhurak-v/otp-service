package com.security.otp.infrastructure.repositories;

import com.security.otp.core.models.entity.OtpEntity;
import com.security.otp.core.models.sort.EntitySort;
import com.security.otp.core.pageable.Pageable;
import com.security.otp.core.repositories.OptRepositoryReader;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public final class MemoryOptRepositoryReader
        extends MemoryRepositoryReader<String, OtpEntity>
        implements OptRepositoryReader {

    public MemoryOptRepositoryReader() {
        super(String.class, OtpEntity.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<OtpEntity> findExpired(
            final Pageable pageable,
            final EntitySort<String, ? extends OtpEntity> sort
    ) {
        final long offset = pageable.getOffset();
        final int limit = pageable.getLimit();
        final Map<String, OtpEntity> storage = this.getStorage();
        final Collection<String> ids = this.getOrderStorage().values();

        final List<OtpEntity> expired = new ArrayList<>();

        if (sort == null) {
            for (final String id : ids) {
                final OtpEntity entity = storage.get(id);
                if (entity != null && entity.isExpired()) {
                    expired.add(entity);
                }
            }

            final int fromIndex = (int) Math.min(offset, expired.size());
            final int toIndex = (int) Math.min(offset + limit, expired.size());

            return List.copyOf(expired.subList(fromIndex, toIndex));
        }

        final Comparator<OtpEntity> comparator = (Comparator<OtpEntity>) sort;
        final long size = offset + limit;
        final PriorityQueue<OtpEntity> heap = new PriorityQueue<>(
                (int) Math.min(size, 1024L),
                comparator.reversed()
        );

        for (final String id : ids) {
            final OtpEntity entity = storage.get(id);
            if (entity != null && entity.isExpired()) {
                if (heap.size() < size) {
                    heap.add(entity);
                } else if (comparator.compare(entity, heap.peek()) < 0) {
                    heap.poll();
                    heap.add(entity);
                }
            }
        }

        final int count = heap.size();
        if (offset >= count) {
            return List.of();
        }

        final List<OtpEntity> sorted = new ArrayList<>(count);
        while (!heap.isEmpty()) {
            sorted.add(heap.poll());
        }
        Collections.reverse(sorted);

        return List.copyOf(sorted.subList(
                (int) offset,
                Math.min((int) offset + limit, count)
        ));
    }
}
