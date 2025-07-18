package com.progressoft.fxdeals.exception;

/**
 * Exception thrown when deal validation fails
 */
public class DealValidationException extends RuntimeException {
    
    public DealValidationException(String message) {
        super(message);
    }
    
    public DealValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 