package com.molkov.otpservice.service.notification;

import com.molkov.otpservice.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smpp.Session;
import org.smpp.pdu.SubmitSM;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsNotificationSender implements NotificationSender {

    private final Session smppSession;

    @Value("${spring.smpp.source_addr}")
    private String sourceAddr;

    @Override
    @Async
    public void send(String message, User user) {
        if (user.getPhoneNumber() == null) {
            log.info("User doesn't have a phone number");
            return;
        }
        try {
            SubmitSM submitSm = new SubmitSM();
            submitSm.setSourceAddr(sourceAddr);
            submitSm.setDestAddr(user.getPhoneNumber());
            submitSm.setShortMessage(message);

            smppSession.submit(submitSm);
            log.info("SMS notification sent successfully: user: {}, message: {}", user.getPhoneNumber(), message);
        } catch (Exception e) {
            log.error("Can't send sms message", e);
        }
    }
}
