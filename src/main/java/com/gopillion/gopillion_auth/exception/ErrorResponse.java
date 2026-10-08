package com.gopillion.gopillion_auth.exception;

import java.time.Instant;

public record ErrorResponse(String code, String message, Instant timestamp) {
    public static ErrorResponse of(ErrorCode c, String message) {
        return new ErrorResponse(c.name(), message, Instant.now());
    }
}
