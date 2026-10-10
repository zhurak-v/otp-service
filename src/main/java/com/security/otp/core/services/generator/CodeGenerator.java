package com.security.otp.core.services.generator;

import com.security.otp.core.dtos.generator.GenerateCodeReq;
import com.security.otp.core.dtos.generator.GenerateCodeRes;
import jakarta.validation.constraints.NotNull;

public interface CodeGenerator {

    @NotNull
    GenerateCodeRes generate(@NotNull final GenerateCodeReq request);

}
