package com.molkov.otpservice.config;

import lombok.extern.slf4j.Slf4j;
import org.smpp.Connection;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransmitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SmppConfig {

    @Value("${spring.smpp.host}")
    private String host;

    @Value("${spring.smpp.port}")
    private int port;

    @Value("${spring.smpp.system_id}")
    private String systemId;

    @Value("${spring.smpp.password}")
    private String password;

    @Value("${spring.smpp.system_type}")
    private String systemType;

    @Value("${spring.smpp.source_addr}")
    private String sourceAddr;

    @Bean
    public Session smppSession() {
        try {
            Connection connection = new TCPIPConnection(host, port);
            Session session = new Session(connection);
            BindRequest bindRequest = new BindTransmitter();
            bindRequest.setSystemId(systemId);
            bindRequest.setPassword(password);
            bindRequest.setSystemType(systemType);
            bindRequest.setInterfaceVersion((byte) 0x34);
            bindRequest.setAddressRange(sourceAddr);

            BindResponse bindResponse = session.bind(bindRequest);
            if (bindResponse.getCommandStatus() != 0) {
                throw new Exception("Bind failed: " + bindResponse.getCommandStatus());
            }

            return session;

        } catch (Exception e) {
            log.error("Can't create smppSession", e);
        }
        return null;
    }

}
