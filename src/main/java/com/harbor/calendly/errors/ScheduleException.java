package com.harbor.calendly.errors;

public class ScheduleException extends RuntimeException implements CalendlyException {

    private ErrorCode errorCode;
    public ScheduleException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ScheduleException(ErrorCode errorCode, Throwable th, String message) {
        super(message, th);
        this.errorCode = errorCode;
    }

    @Override
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
