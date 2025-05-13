package com.molkov.otpservice.dto;


import com.molkov.otpservice.model.user.UserRole;
import jakarta.validation.constraints.*;
import lombok.Builder;

/**
 * DTO для {@link com.molkov.otpservice.model.user.User}
 */
@Builder
public record UserDto(
        @NotNull @NotBlank String username,
        @NotNull @NotBlank String password,
        @NotNull UserRole role,
        @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "Телефон должен начинаться на +7 или 8 и содержать 11 цифр")
        String phoneNumber,
        @Email(message = "Некорректный формат e-mail")
        String email,
        @Positive Long telegramId) {
}
