package com.molkov.otpservice.controller;

import com.molkov.otpservice.dto.otp.GenerateOtpCodeDto;
import com.molkov.otpservice.dto.otp.ValidateOtpCodeDto;
import com.molkov.otpservice.service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/otp")
public class OtpController {

    private final OtpService otpService;

    @PostMapping
    public String generateOtpCode(@RequestBody GenerateOtpCodeDto generateOtpCodeDto) {
        return otpService.generateCode(generateOtpCodeDto);
    }

    @PostMapping("/validate")
    public void validateOtpCode(@RequestBody ValidateOtpCodeDto validateOtpCodeDto) {
        otpService.validateCode(validateOtpCodeDto);
    }
}
