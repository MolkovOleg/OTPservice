package com.molkov.otpservice.controller;

import com.molkov.otpservice.dto.UserDto;
import com.molkov.otpservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody UserDto request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDto request) {
        return authService.login(request);
    }
}
