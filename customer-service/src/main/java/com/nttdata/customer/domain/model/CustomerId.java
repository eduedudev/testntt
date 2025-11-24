package com.nttdata.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * Customer ID Value Object
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class CustomerId {
    private final UUID value;

    @Override
    public String toString() {
        return value.toString();
    }
}
