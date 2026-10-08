package com.gopillion.gopillion_auth.service;

import com.gopillion.gopillion_auth.config.AuthProperties;
import com.gopillion.gopillion_auth.exception.ApiException;
import com.gopillion.gopillion_auth.exception.ErrorCode;
import com.gopillion.gopillion_auth.sms.SmsSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redis;
    private final SmsSender smsSender;
    private final AuthProperties props;
    private final SecureRandom random = new SecureRandom();

    private static String otpKey(String phone)      { return "otp:" + phone; }
    private static String attemptsKey(String phone) { return "otp:attempts:" + phone; }
    private static String sendsKey(String phone)    { return "otp:sends:" + phone; }

    public void sendOtp(String phone) {
        AuthProperties.Otp cfg = props.otp();

        Long sends = redis.opsForValue().increment(sendsKey(phone));
        if (sends != null && sends == 1) {
            redis.expire(sendsKey(phone), Duration.ofSeconds(cfg.sendWindowSeconds()));
        }
        if (sends != null && sends > cfg.maxSendsPerWindow()) {
            throw new ApiException(ErrorCode.OTP_RATE_LIMITED);
        }

        String otp = (cfg.fixedOtp() != null && !cfg.fixedOtp().isBlank())
                ? cfg.fixedOtp()
                : String.format("%06d", random.nextInt(1_000_000));

        redis.opsForValue().set(otpKey(phone), Hashing.sha256Hex(otp), Duration.ofSeconds(cfg.ttlSeconds()));
        redis.delete(attemptsKey(phone));
        smsSender.send(phone, "Your RideShare OTP is " + otp + ". Valid for "
                + (cfg.ttlSeconds() / 60) + " minutes. Do not share it.");
    }

    public void verifyOtp(String phone, String otp) {
        AuthProperties.Otp cfg = props.otp();

        Long attempts = redis.opsForValue().increment(attemptsKey(phone));
        if (attempts != null && attempts == 1) {
            redis.expire(attemptsKey(phone), Duration.ofSeconds(cfg.ttlSeconds()));
        }
        if (attempts != null && attempts > cfg.maxAttempts()) {
            redis.delete(otpKey(phone));
            throw new ApiException(ErrorCode.OTP_ATTEMPTS_EXCEEDED);
        }

        String stored = redis.opsForValue().get(otpKey(phone));
        if (stored == null) throw new ApiException(ErrorCode.OTP_EXPIRED);
        if (!stored.equals(Hashing.sha256Hex(otp))) throw new ApiException(ErrorCode.OTP_INVALID);

        redis.delete(List.of(otpKey(phone), attemptsKey(phone)));
    }
}
