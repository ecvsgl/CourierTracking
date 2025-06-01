package com.efecavusoglu.couriertracking.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * RestControllerAdvice class handles all exceptions thrown by the application.
 */
@RestControllerAdvice
public class RestErrorHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CourierAppExceptionTemplate> handleException(IllegalArgumentException e) {
        CourierAppExceptionTemplate template = createExceptionTemplate(e.getMessage());
        return new ResponseEntity<>(template, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InsufficientDataException.class, EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CourierAppExceptionTemplate> handleException(Exception e) {
        CourierAppExceptionTemplate template = createExceptionTemplate(e.getMessage());
        return new ResponseEntity<>(template, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({PolicyNotFoundException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CourierAppExceptionTemplate> handleException(PolicyNotFoundException e) {
        CourierAppExceptionTemplate template = createExceptionTemplate(e.getMessage());
        return new ResponseEntity<>(template, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private CourierAppExceptionTemplate createExceptionTemplate(String message) {
        return CourierAppExceptionTemplate.builder()
                .exceptionMessage(message)
                .exceptionDate(LocalDateTime.now())
                .build();
    }
}
