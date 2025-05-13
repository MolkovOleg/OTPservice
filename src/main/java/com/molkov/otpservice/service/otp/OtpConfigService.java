package com.molkov.otpservice.service.otp;

import com.molkov.otpservice.dto.otp.OtpConfigDto;
import com.molkov.otpservice.model.otp.OtpConfig;
import com.molkov.otpservice.repository.OtpConfigRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpConfigService {

    private final OtpConfigRepository otpConfigRepository;

    @Transactional(readOnly = true)
    public OtpConfigDto getOtpConfigDto() {
        OtpConfig config = getOtpConfig();
        return new OtpConfigDto(config.getCodeLength(), config.getTtlSeconds());
    }

    @Transactional(readOnly = true)
    public OtpConfig getOtpConfig() {
        return otpConfigRepository.findOtpConfigById(1)
                .orElseThrow(() -> {
                    log.info("OTP config not found");
                    return new EntityExistsException("OTP config not found");
                });
    }

    @Transactional
    public OtpConfigDto update(OtpConfigDto otpConfigDto) {
        OtpConfig config = getOtpConfig();
        if (otpConfigDto.codeLength() != null) {
            config.setCodeLength(otpConfigDto.codeLength());
        }
        if (otpConfigDto.ttlSeconds() != null) {
            config.setTtlSeconds(otpConfigDto.ttlSeconds());
        }
        log.info("OTP config updated {}", config);
        return new OtpConfigDto(config.getCodeLength(), config.getTtlSeconds());
    }

    @Transactional(readOnly = true)
    public boolean existsConfig() {
        return otpConfigRepository.existsById(1);
    }

    @Transactional
    public OtpConfigDto create(OtpConfigDto otpConfigDto) {
        if (existsConfig()) {
            log.info("OTP config already exists");
            throw new EntityExistsException("OTP config already exists");
        }
        OtpConfig config = new OtpConfig();
        config.setCodeLength(otpConfigDto.codeLength());
        config.setTtlSeconds(otpConfigDto.ttlSeconds());

        OtpConfig otpConfig = otpConfigRepository.save(config);
        log.info("OTP config created {}", otpConfig);
        return new OtpConfigDto(otpConfig.getCodeLength(), otpConfig.getTtlSeconds());
    }

}
