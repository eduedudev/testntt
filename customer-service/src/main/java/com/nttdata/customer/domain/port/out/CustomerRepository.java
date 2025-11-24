package com.nttdata.customer.domain.port.out;

import com.nttdata.customer.domain.model.Customer;
import com.nttdata.customer.domain.model.CustomerId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Output port for Customer persistence operations
 * This is the port that will be implemented by the infrastructure layer
 */
public interface CustomerRepository {
    
    /**
     * Save a customer
     */
    Mono<Customer> save(Customer customer);
    
    /**
     * Find a customer by ID
     */
    Mono<Customer> findById(CustomerId customerId);
    
    /**
     * Find a customer by identification
     */
    Mono<Customer> findByIdentification(String identification);
    
    /**
     * Find all customers
     */
    Flux<Customer> findAll();
    
    /**
     * Delete a customer by ID
     */
    Mono<Void> deleteById(CustomerId customerId);
    
    /**
     * Check if a customer exists by identification
     */
    Mono<Boolean> existsByIdentification(String identification);
}
