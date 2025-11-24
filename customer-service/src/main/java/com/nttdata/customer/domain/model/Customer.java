package com.nttdata.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Customer Aggregate Root - DDD pattern
 * A customer is a person with authentication and status
 */
@Getter
@Builder
@AllArgsConstructor
public class Customer {
    private final CustomerId customerId;
    private final Person person;
    private final String password;
    private final boolean status;

    /**
     * Factory method to create a new customer
     */
    public static Customer create(Person person, String password, boolean status) {
        Customer customer = Customer.builder()
                .customerId(new CustomerId(UUID.randomUUID()))
                .person(person)
                .password(password)
                .status(status)
                .build();
        
        customer.validate();
        return customer;
    }

    /**
     * Domain validation rules
     */
    public void validate() {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long");
        }
        
        person.validate();
    }

    /**
     * Business method to update customer information
     */
    public Customer update(Person person, String password, boolean status) {
        return Customer.builder()
                .customerId(this.customerId)
                .person(person)
                .password(password)
                .status(status)
                .build();
    }

    /**
     * Business method to activate customer
     */
    public Customer activate() {
        return Customer.builder()
                .customerId(this.customerId)
                .person(this.person)
                .password(this.password)
                .status(true)
                .build();
    }

    /**
     * Business method to deactivate customer
     */
    public Customer deactivate() {
        return Customer.builder()
                .customerId(this.customerId)
                .person(this.person)
                .password(this.password)
                .status(false)
                .build();
    }
}
