package com.nttdata.account.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * Movement ID Value Object
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class MovementId {
    private final UUID value;

    public static MovementId of(String id) {
        return new MovementId(UUID.fromString(id));
    }

    public static MovementId of(UUID id) {
        return new MovementId(id);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
