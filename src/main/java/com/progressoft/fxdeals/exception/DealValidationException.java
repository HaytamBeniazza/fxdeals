package com.progressoft.fxdeals.exception;

public class DealValidationException extends RuntimeException {
    
    public DealValidationException(String message) {
        super(message);
    }
    
    public DealValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 