package com.nttdata.customer.application.usecase;

import com.nttdata.customer.domain.model.Customer;
import com.nttdata.customer.domain.model.CustomerId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Input port - Use cases for Customer management
 */
public interface CustomerUseCase {
    
    /**
     * Create a new customer
     */
    Mono<Customer> createCustomer(Customer customer);
    
    /**
     * Get customer by ID
     */
    Mono<Customer> getCustomerById(CustomerId customerId);
    
    /**
     * Get all customers
     */
    Flux<Customer> getAllCustomers();
    
    /**
     * Update customer
     */
    Mono<Customer> updateCustomer(CustomerId customerId, Customer customer);
    
    /**
     * Delete customer
     */
    Mono<Void> deleteCustomer(CustomerId customerId);
}
