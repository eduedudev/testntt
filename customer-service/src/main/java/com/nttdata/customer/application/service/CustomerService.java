package com.nttdata.customer.application.service;

import com.nttdata.customer.application.usecase.CustomerUseCase;
import com.nttdata.customer.domain.exception.CustomerAlreadyExistsException;
import com.nttdata.customer.domain.exception.CustomerNotFoundException;
import com.nttdata.customer.domain.model.Customer;
import com.nttdata.customer.domain.model.CustomerId;
import com.nttdata.customer.domain.port.out.CustomerEventPublisher;
import com.nttdata.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Customer Service - Application layer
 * Implements the use cases orchestrating domain logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService implements CustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerEventPublisher eventPublisher;

    @Override
    public Mono<Customer> createCustomer(Customer customer) {
        log.info("Creating customer with identification: {}", customer.getPerson().getIdentification());
        
        return customerRepository.existsByIdentification(customer.getPerson().getIdentification())
                .flatMap(exists -> {
                    if (exists) {
                        log.error("Customer already exists with identification: {}", 
                                customer.getPerson().getIdentification());
                        return Mono.error(new CustomerAlreadyExistsException(
                                "Customer already exists with identification: " + 
                                customer.getPerson().getIdentification()));
                    }
                    
                    customer.validate();
                    return customerRepository.save(customer)
                            .flatMap(savedCustomer -> 
                                eventPublisher.publishCustomerCreated(
                                        savedCustomer.getCustomerId(),
                                        savedCustomer.getPerson().getName())
                                .thenReturn(savedCustomer)
                            )
                            .doOnSuccess(savedCustomer -> 
                                log.info("Customer created successfully with ID: {}", 
                                        savedCustomer.getCustomerId()));
                });
    }

    @Override
    public Mono<Customer> getCustomerById(CustomerId customerId) {
        log.info("Fetching customer with ID: {}", customerId);
        
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with ID: " + customerId)))
                .doOnSuccess(customer -> 
                    log.info("Customer found with ID: {}", customerId));
    }

    @Override
    public Flux<Customer> getAllCustomers() {
        log.info("Fetching all customers");
        
        return customerRepository.findAll()
                .doOnComplete(() -> log.info("All customers fetched successfully"));
    }

    @Override
    public Mono<Customer> updateCustomer(CustomerId customerId, Customer customer) {
        log.info("Updating customer with ID: {}", customerId);
        
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with ID: " + customerId)))
                .flatMap(existingCustomer -> {
                    Customer updatedCustomer = Customer.builder()
                            .customerId(customerId)
                            .person(customer.getPerson())
                            .password(customer.getPassword())
                            .status(customer.isStatus())
                            .build();
                    
                    updatedCustomer.validate();
                    
                    return customerRepository.save(updatedCustomer)
                            .flatMap(savedCustomer ->
                                eventPublisher.publishCustomerUpdated(
                                        savedCustomer.getCustomerId(),
                                        savedCustomer.getPerson().getName())
                                .thenReturn(savedCustomer)
                            );
                })
                .doOnSuccess(updatedCustomer -> 
                    log.info("Customer updated successfully with ID: {}", customerId));
    }

    @Override
    public Mono<Void> deleteCustomer(CustomerId customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with ID: " + customerId)))
                .flatMap(customer -> 
                    customerRepository.deleteById(customerId)
                            .then(eventPublisher.publishCustomerDeleted(customerId))
                )
                .doOnSuccess(v -> 
                    log.info("Customer deleted successfully with ID: {}", customerId));
    }
}
