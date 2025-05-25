package com.efecavusoglu.couriertracking.exception;

/**
 * Custom exception for insufficient data.
 */
public class InsufficientDataException extends RuntimeException{
    public InsufficientDataException(String msg) {
        super(msg);
    }
}

