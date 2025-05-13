package com.molkov.otpservice.service.otp;

import com.molkov.otpservice.dto.otp.GenerateOtpCodeDto;
import com.molkov.otpservice.dto.otp.ValidateOtpCodeDto;
import com.molkov.otpservice.excpeption.OtpCodeExpiredException;
import com.molkov.otpservice.excpeption.OtpCodeNotActiveException;
import com.molkov.otpservice.model.otp.OtpCode;
import com.molkov.otpservice.model.otp.OtpConfig;
import com.molkov.otpservice.model.otp.OtpStatus;
import com.molkov.otpservice.model.user.User;
import com.molkov.otpservice.repository.OtpCodeRepository;
import com.molkov.otpservice.service.notification.NotificationSender;
import com.molkov.otpservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpConfigService otpConfigService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();
    private final UserService userService;
    private final OtpCodeRepository otpCodeRepository;
    private final List<NotificationSender> notificationSenders;

    @Transactional
    public String generateCode(GenerateOtpCodeDto otpCodeDto) {
        OtpConfig config = otpConfigService.getOtpConfig();
        String code = generateRandomCode(config.getCodeLength());
        User user = userService.getAuthorizedUser();

        OtpCode otpCode = OtpCode.builder()
                .code(code)
                .user(user)
                .status(OtpStatus.ACTIVE)
                .operationId(otpCodeDto.operationId())
                .expiresAt(LocalDateTime.now().plusSeconds(config.getTtlSeconds()))
                .build();
        otpCodeRepository.save(otpCode);
        log.info("Generated OTP code: {}, user: {}", otpCode, user);
        notificationSenders.forEach(notificationSender -> {
            notificationSender.send("Ваш код подтверждения: " + code, user);
        });
        return code;
    }

    @Transactional
    public void validateCode(ValidateOtpCodeDto validateOtpCodeDto) {
        User user = userService.getAuthorizedUser();

        OtpCode otpCode = getOptCodeWithUser(validateOtpCodeDto.code(), user.getId());
        if (!otpCode.getStatus().equals(OtpStatus.ACTIVE)) {
            log.info("OTP code {} has not been active", otpCode);
            throw new OtpCodeNotActiveException();
        }
        if (isExpiredOtpCode(otpCode)) {
            log.info("OTP code {} has expired", otpCode);
            throw new OtpCodeExpiredException();
        }
        otpCode.setStatus(OtpStatus.USED);
        log.info("OTP code {} has been used", otpCode);
    }

    private boolean isExpiredOtpCode(OtpCode otpCode) {
        return otpCode.getExpiresAt().isBefore(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public OtpCode getOptCodeWithUser(String code, Long userId) {
        return otpCodeRepository.findByCodeAndUserId(code, userId)
                .orElseThrow(() -> {
                    log.info("Otp code not found: code={}, userid={}", code, userId);
                    return new EntityNotFoundException("Otp code not found");
                });
    }

    @Transactional(readOnly = true)
    public OtpCode getOptCode(String code) {
        return otpCodeRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.info("Otp code not found: code={}", code);
                    return new EntityNotFoundException("Otp code not found");
                });
    }

    private String generateRandomCode(Integer codeLength) {
        StringBuilder sb = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            int idx = secureRandom.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(idx));
        }
        return sb.toString();
    }

    @Scheduled(fixedDelayString = "${spring.otp.check-expired-delay-ms}")
    @Transactional
    public void checkExpiredOtpCode() {
        int expiredOtpCode = otpCodeRepository.expireActiveCodes(LocalDateTime.now());
        log.info("OTP codes expired: {}", expiredOtpCode);
    }

    @Transactional
    public void deleteOtpCodeByUserId(Long userId) {
        otpCodeRepository.deleteAllByUser_Id(userId);
    }
}
