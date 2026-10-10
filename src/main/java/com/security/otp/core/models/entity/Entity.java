package com.security.otp.core.models.entity;

import com.security.otp.core.models.auditable.Auditable;
import com.security.otp.core.models.identifiable.Identifiable;

import java.io.Serializable;

public interface Entity<ID extends Serializable> extends Identifiable<ID>, Auditable {
}
