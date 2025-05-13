package com.molkov.otpservice.controller;

import com.molkov.otpservice.dto.UserDto;
import com.molkov.otpservice.dto.otp.OtpConfigDto;
import com.molkov.otpservice.service.AdminService;
import com.molkov.otpservice.service.UserService;
import com.molkov.otpservice.service.otp.OtpConfigService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final OtpConfigService otpConfigService;

    @GetMapping("/config")
    public OtpConfigDto getOtpConfig() {
        return otpConfigService.getOtpConfigDto();
    }

    @PostMapping("/config")
    public OtpConfigDto createOtpConfig(@RequestBody OtpConfigDto otpConfigDto) {
        return otpConfigService.create(otpConfigDto);
    }

    @PutMapping("/config")
    public OtpConfigDto updateOtpConfig(@RequestBody OtpConfigDto otpConfigDto) {
        return otpConfigService.update(otpConfigDto);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserAndOtpCodes(@Valid @NotNull @Positive @PathVariable Long id) {
        adminService.deleteUserAndOtpCodes(id);
    }
}
