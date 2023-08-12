package com.harbor.calendly.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_TIMEZONE(HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_EXISTS(HttpStatus.NOT_FOUND),
    SCHEDULE_ALREADY_EXISTS(HttpStatus.PRECONDITION_FAILED),

    USER_ALREADY_EXISTS(HttpStatus.PRECONDITION_FAILED),
    USER_NOT_EXISTS(HttpStatus.NOT_FOUND);

    private int httpStatusCode;

    ErrorCode(HttpStatus httpStatus) {
        this.httpStatusCode = httpStatus.value();
    }

}
