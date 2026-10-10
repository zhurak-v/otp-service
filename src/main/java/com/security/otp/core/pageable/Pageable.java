package com.security.otp.core.pageable;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public interface Pageable {
    @PositiveOrZero
    long getOffset();

    @Positive
    int getLimit();
}
