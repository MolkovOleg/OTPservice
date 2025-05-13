package com.molkov.otpservice.service.notification;

import com.molkov.otpservice.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    @Override
    @Async
    public void send(String message, User user) {
        String to = user.getEmail();
        if (to == null) {
            log.info("User don't have an email address");
            return;
        }

        var mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setFrom(from);
        mail.setSubject("OTP Service");
        mail.setText(message);

        mailSender.send(mail);
        log.info("Send mail to {} successfully", to);
    }
}
