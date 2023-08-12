package com.harbor.calendly.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_ALREADY_EXISTS(HttpStatus.PRECONDITION_FAILED),
    USER_NOT_EXISTS(HttpStatus.NOT_FOUND);

    private int httpStatusCode;

    ErrorCode(HttpStatus httpStatus) {
        this.httpStatusCode = httpStatus.value();
    }

}
