package com.security.otp.core.models.entity;

import com.security.otp.core.models.expirable.Expirable;

import java.io.Serializable;

public interface ExpirableEntity<ID extends Serializable> extends Entity<ID>, Expirable {
}
