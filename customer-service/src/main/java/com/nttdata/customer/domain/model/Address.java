package com.nttdata.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Address Value Object
 */
@Getter
@AllArgsConstructor
public class Address {
    private final String value;

    public void validate() {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Address value cannot be empty");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
