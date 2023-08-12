package com.harbor.calendly.errors;

public interface CalendlyException {

    ErrorCode getErrorCode();

    String getMessage();
}
