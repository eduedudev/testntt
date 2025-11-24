package com.nttdata.customer.domain.exception;

/**
 * Exception thrown when trying to create a customer that already exists
 */
public class CustomerAlreadyExistsException extends RuntimeException {
    
    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
}
