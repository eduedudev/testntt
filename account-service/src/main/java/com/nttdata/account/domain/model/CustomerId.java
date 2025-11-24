package com.nttdata.account.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * Customer ID Value Object - referenced from Customer Service
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class CustomerId {
    private final UUID value;

    public static CustomerId of(String id) {
        return new CustomerId(UUID.fromString(id));
    }

    public static CustomerId of(UUID id) {
        return new CustomerId(id);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
