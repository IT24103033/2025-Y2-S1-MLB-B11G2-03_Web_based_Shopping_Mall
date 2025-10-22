package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.exception;

/**
 * Custom exception for validation errors in the notification system
 * Simple exception class that extends RuntimeException for easy error handling
 */
public class ValidationException extends RuntimeException {
    
    /**
     * Create a validation exception with a message
     * @param message The error message describing what validation failed
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Create a validation exception with a message and underlying cause
     * @param message The error message
     * @param cause The underlying exception that caused this validation error
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
