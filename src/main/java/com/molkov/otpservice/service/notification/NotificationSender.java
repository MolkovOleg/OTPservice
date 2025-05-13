package com.molkov.otpservice.service.notification;

import com.molkov.otpservice.model.user.User;

public interface NotificationSender {
    void send(String message, User user);
}
