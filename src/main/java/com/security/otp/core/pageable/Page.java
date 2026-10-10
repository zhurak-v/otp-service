package com.security.otp.core.pageable;

public final class Page implements Pageable {

    private final long offset;
    private final int limit;

    public Page(final long offset, final int limit) {
        if (offset < 0) {
            throw new IllegalArgumentException();
        }
        if (limit <= 0) {
            throw new IllegalArgumentException();
        }
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public long getOffset() {
        return this.offset;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

}
