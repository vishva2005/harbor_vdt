package com.harbor.calendly.errors;

public class AvailabilityException extends RuntimeException implements CalendlyException {

    private ErrorCode errorCode;
    public AvailabilityException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AvailabilityException(ErrorCode errorCode, Throwable th, String message) {
        super(message, th);
        this.errorCode = errorCode;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
