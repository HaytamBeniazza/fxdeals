package com.progressoft.fxdeals.exception;

/**
 * Exception thrown when attempting to save a deal with a duplicate unique ID
 */
public class DuplicateDealException extends RuntimeException {
    
    private final String dealUniqueId;
    
    public DuplicateDealException(String dealUniqueId) {
        super("Deal with unique ID '" + dealUniqueId + "' already exists in the system");
        this.dealUniqueId = dealUniqueId;
    }
    
    public DuplicateDealException(String dealUniqueId, String message) {
        super(message);
        this.dealUniqueId = dealUniqueId;
    }
    
    public String getDealUniqueId() {
        return dealUniqueId;
    }
} 