package com.molkov.otpservice.model.otp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "otp_config")
public class OtpConfig {

    @Id
    private Integer id = 1;

    @Column(name = "code_length", nullable = false)
    private Integer codeLength;

    @Column(name = "ttl_seconds", nullable = false)
    private Long ttlSeconds;
}
