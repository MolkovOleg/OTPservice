package com.molkov.otpservice.excpeption;

public class OtpCodeExpiredException extends RuntimeException {
    public OtpCodeExpiredException() {
        super("OTP code expired");
    }
}
