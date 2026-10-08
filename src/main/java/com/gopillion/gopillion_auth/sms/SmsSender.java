package com.gopillion.gopillion_auth.sms;

public interface SmsSender {
    void send(String phoneNumber, String message);
}
