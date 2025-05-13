package com.molkov.otpservice.dto.otp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ValidateOtpCodeDto(
        @NotNull @NotBlank String code,
        @NotNull Long operationId
) {
}
