package com.harbor.calendly.errors;

import lombok.Getter;

@Getter
public class BookingSlotException extends RuntimeException implements CalendlyException {

    private final ErrorCode errorCode;

    public BookingSlotException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BookingSlotException(ErrorCode errorCode, Throwable th, String message) {
        super(message, th);
        this.errorCode = errorCode;
    }
}
