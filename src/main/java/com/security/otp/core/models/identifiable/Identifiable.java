package com.security.otp.core.models.identifiable;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public interface Identifiable<ID extends Serializable> {
    @NotNull
    ID getId();
}
