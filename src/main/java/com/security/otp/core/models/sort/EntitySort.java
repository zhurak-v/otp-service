package com.security.otp.core.models.sort;

import java.io.Serializable;
import java.util.function.Function;

import com.security.otp.core.models.entity.Entity;
import jakarta.validation.constraints.NotNull;

@FunctionalInterface
public interface EntitySort<ID extends Serializable, E extends Entity<ID>> extends Sort<E> {

    @NotNull
    static <ID extends Serializable, E extends Entity<ID>, U extends Comparable<? super U>> EntitySort<ID, E> comparing(
            @NotNull final Function<? super E, ? extends U> keyExtractor
    ) {
        return (e1, e2) -> {
            final U u1 = keyExtractor.apply(e1);
            final U u2 = keyExtractor.apply(e2);
            if (u1 == null && u2 == null) {
                return 0;
            }
            if (u1 == null) {
                return -1;
            }
            if (u2 == null) {
                return 1;
            }
            return u1.compareTo(u2);
        };
    }

    @NotNull
    static <ID extends Serializable & Comparable<? super ID>, E extends Entity<ID>> EntitySort<ID, E> byId() {
        return comparing(Entity::getId);
    }

    @NotNull
    static <ID extends Serializable, E extends Entity<ID>> EntitySort<ID, E> byCreatedAt() {
        return comparing(Entity::getCreatedAt);
    }

    @NotNull
    default EntitySort<ID, E> reversed() {
        return (e1, e2) -> EntitySort.this.compare(e2, e1);
    }

}