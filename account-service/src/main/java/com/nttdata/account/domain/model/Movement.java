package com.nttdata.account.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Movement Aggregate Root - DDD pattern
 * Represents a transaction on an account
 */
@Getter
@Builder
@AllArgsConstructor
public class Movement {
    private final MovementId movementId;
    private final LocalDateTime date;
    private final MovementType movementType;
    private final BigDecimal value;
    private final BigDecimal balance;
    private final AccountId accountId;
    private final String accountNumber; // Denormalized for reporting

    /**
     * Factory method to create a new movement
     */
    public static Movement create(MovementType movementType, BigDecimal value, 
                                  BigDecimal balance, AccountId accountId, String accountNumber) {
        Movement movement = Movement.builder()
                .movementId(new MovementId(UUID.randomUUID()))
                .date(LocalDateTime.now())
                .movementType(movementType)
                .value(value)
                .balance(balance)
                .accountId(accountId)
                .accountNumber(accountNumber)
                .build();
        
        movement.validate();
        return movement;
    }

    /**
     * Domain validation rules
     */
    public void validate() {
        if (movementId == null) {
            throw new IllegalArgumentException("Movement ID cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (movementType == null) {
            throw new IllegalArgumentException("Movement type cannot be null");
        }
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Value must be greater than zero");
        }
        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
    }

    /**
     * Factory method to reconstitute a movement from persistence
     */
    public static Movement reconstitute(MovementId movementId, LocalDateTime date,
                                       MovementType movementType, BigDecimal value,
                                       BigDecimal balance, AccountId accountId, String accountNumber) {
        return Movement.builder()
                .movementId(movementId)
                .date(date)
                .movementType(movementType)
                .value(value)
                .balance(balance)
                .accountId(accountId)
                .accountNumber(accountNumber)
                .build();
    }
}
