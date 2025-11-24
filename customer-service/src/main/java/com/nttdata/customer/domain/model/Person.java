package com.nttdata.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Person Value Object - Represents the base person information
 * This is a Value Object in DDD terms
 */
@Getter
@Builder
@AllArgsConstructor
public class Person {
    private final String name;
    private final Gender gender;
    private final String identification;
    private final Address address;
    private final Phone phone;

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        if (identification == null || identification.isBlank()) {
            throw new IllegalArgumentException("Identification cannot be empty");
        }
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (phone == null) {
            throw new IllegalArgumentException("Phone cannot be null");
        }
        
        address.validate();
        phone.validate();
    }
}
