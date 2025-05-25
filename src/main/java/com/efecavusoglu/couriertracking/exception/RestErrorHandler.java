package com.efecavusoglu.couriertracking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestErrorHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CourierAppExceptionTemplate> handleException(IllegalArgumentException e) {
        CourierAppExceptionTemplate template = createExceptionTemplate(e.getMessage());
        return new ResponseEntity<>(template, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InsufficientDataException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CourierAppExceptionTemplate> handleException(InsufficientDataException e) {
        CourierAppExceptionTemplate template = createExceptionTemplate(e.getMessage());
        return new ResponseEntity<>(template, HttpStatus.NOT_FOUND);
    }

    private CourierAppExceptionTemplate createExceptionTemplate(String message) {
        return CourierAppExceptionTemplate.builder()
                .exceptionMessage(message)
                .exceptionDate(LocalDateTime.now())
                .build();
    }
}
