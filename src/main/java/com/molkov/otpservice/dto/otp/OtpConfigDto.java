package com.molkov.otpservice.dto.otp;

/**
 * DTO для {@link com.molkov.otpservice.model.otp.OtpConfig}
 */
public record OtpConfigDto(Integer codeLength, Long ttlSeconds) {
}
