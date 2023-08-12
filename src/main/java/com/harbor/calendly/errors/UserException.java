package com.harbor.calendly.errors;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException implements CalendlyException {

    private ErrorCode errorCode;
    public UserException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public UserException(ErrorCode errorCode, Throwable th, String message) {
        super(message, th);
        this.errorCode = errorCode;
    }
}
