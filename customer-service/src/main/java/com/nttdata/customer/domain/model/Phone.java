package com.nttdata.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Phone Value Object
 */
@Getter
@AllArgsConstructor
public class Phone {
    private final String number;

    public void validate() {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        if (!number.matches("\\d{9,15}")) {
            throw new IllegalArgumentException("Phone number must contain 9-15 digits");
        }
    }

    @Override
    public String toString() {
        return number;
    }
}
