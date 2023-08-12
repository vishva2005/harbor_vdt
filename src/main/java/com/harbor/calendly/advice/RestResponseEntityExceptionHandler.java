package com.harbor.calendly.advice;

import com.harbor.calendly.errors.CalendlyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(RuntimeException ex) {
        int httpStatusCode = 500;
        String message = "Unknown Error";
        String errorCode = "UNKNOWN_ERROR";
        if (ex instanceof CalendlyException) {
            CalendlyException exception = (CalendlyException)ex;
            httpStatusCode = exception.getErrorCode().getHttpStatusCode();
            errorCode = exception.getErrorCode().name();
            message = exception.getMessage();
        } else {
            logger.atError()
                    .setMessage("unknown exception occurred")
                    .setCause(ex)
                    .log();
        }

        return ResponseEntity.status(httpStatusCode)
                .body(new ErrorResponse(errorCode, message));
    }

}
