package com.nttdata.account.domain.exception;

/**
 * Exception thrown when a customer is not found (from customer service)
 */
public class CustomerNotFoundException extends RuntimeException {
    
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
