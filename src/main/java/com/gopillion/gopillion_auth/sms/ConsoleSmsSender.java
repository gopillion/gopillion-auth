package com.gopillion.gopillion_auth.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Local-dev sender: prints the SMS to the log. Replace with a Kafka/MSG91 sender for prod. */
@Slf4j
@Component
@Profile("!prod")
public class ConsoleSmsSender implements SmsSender {
    @Override
    public void send(String phoneNumber, String message) {
        log.info("[DEV SMS] to={} body={}", phoneNumber, message);
    }
}
