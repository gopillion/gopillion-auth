package com.gopillion.gopillion_auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VerifyOtpRequest(
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "must be 10-15 digits") String phoneNumber,
        @NotBlank @Pattern(regexp = "^[0-9]{6}$", message = "must be 6 digits") String otp,
        @NotBlank @Size(max = 100) String deviceId,
        @Size(max = 255) String deviceInfo,
        String fcmToken) {}
