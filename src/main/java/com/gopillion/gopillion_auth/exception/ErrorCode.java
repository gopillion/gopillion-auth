package com.gopillion.gopillion_auth.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_PHONE(HttpStatus.BAD_REQUEST, "Invalid phone number"),
    OTP_RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "Too many OTP requests, try again later"),
    OTP_INVALID(HttpStatus.BAD_REQUEST, "Incorrect OTP"),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST, "OTP expired or not requested"),
    OTP_ATTEMPTS_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Too many incorrect attempts, request a new OTP"),
    USER_BANNED(HttpStatus.FORBIDDEN, "Account is suspended"),
    TEMP_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Signup session invalid or expired"),
    REFRESH_INVALID(HttpStatus.UNAUTHORIZED, "Refresh token invalid or expired"),
    REFRESH_REUSED(HttpStatus.UNAUTHORIZED, "Refresh token reuse detected, please login again"),
    INVALID_MODE(HttpStatus.BAD_REQUEST, "Mode must be RIDER or DRIVER"),
    VALIDATION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed"),
    UPSTREAM_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Dependent service unavailable"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }
    public HttpStatus status() { return status; }
    public String defaultMessage() { return defaultMessage; }
}
