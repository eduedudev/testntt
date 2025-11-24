package com.nttdata.account.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * Account ID Value Object
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class AccountId {
    private final UUID value;

    public static AccountId of(String id) {
        return new AccountId(UUID.fromString(id));
    }

    public static AccountId of(UUID id) {
        return new AccountId(id);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
