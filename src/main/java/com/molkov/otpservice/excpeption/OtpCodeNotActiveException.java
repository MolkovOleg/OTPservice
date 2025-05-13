package com.molkov.otpservice.excpeption;

public class OtpCodeNotActiveException extends RuntimeException {
    public OtpCodeNotActiveException() {
        super("OTP code not active");
    }
}
