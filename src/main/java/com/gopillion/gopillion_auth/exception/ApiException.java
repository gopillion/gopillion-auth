package com.gopillion.gopillion_auth.exception;

public class ApiException extends RuntimeException {
    private final ErrorCode code;

    public ApiException(ErrorCode code) {
        super(code.defaultMessage());
        this.code = code;
    }
    public ErrorCode getCode() { return code; }
}
