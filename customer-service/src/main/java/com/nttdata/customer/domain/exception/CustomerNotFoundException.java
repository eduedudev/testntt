package com.nttdata.customer.domain.exception;

/**
 * Exception thrown when a customer is not found
 */
public class CustomerNotFoundException extends RuntimeException {
    
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
