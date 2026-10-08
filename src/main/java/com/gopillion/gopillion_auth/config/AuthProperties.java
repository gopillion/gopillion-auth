package com.gopillion.gopillion_auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public record AuthProperties(Jwt jwt, Refresh refresh, Otp otp) {
    public record Jwt(String issuer, String privateKeyPath, String publicKeyPath,
                      long accessTtlMinutes, long tempTtlMinutes) {}
    public record Refresh(long ttlDays) {}
    public record Otp(long ttlSeconds, int maxAttempts, int maxSendsPerWindow,
                      long sendWindowSeconds, String fixedOtp) {}
}
