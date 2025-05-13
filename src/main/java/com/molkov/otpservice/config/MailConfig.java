package com.molkov.otpservice.config;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public GreenMail greenMail() {
        return new GreenMail(ServerSetupTest.SMTP);
    }
}
