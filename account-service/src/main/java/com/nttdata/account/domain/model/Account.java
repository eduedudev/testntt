package com.nttdata.account.domain.model;

import com.nttdata.account.domain.exception.InsufficientBalanceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Account Aggregate Root - DDD pattern
 */
@Getter
@Builder
@AllArgsConstructor
public class Account {
    private final AccountId accountId;
    private final String accountNumber;
    private final AccountType accountType;
    private final BigDecimal currentBalance;
    private final boolean status;
    private final CustomerId customerId;
    private final String customerName; // Denormalized for reporting

    /**
     * Factory method to create a new account
     */
    public static Account create(String accountNumber, AccountType accountType, 
                                 BigDecimal initialBalance, boolean status, 
                                 CustomerId customerId, String customerName) {
        Account account = Account.builder()
                .accountId(new AccountId(UUID.randomUUID()))
                .accountNumber(accountNumber)
                .accountType(accountType)
                .currentBalance(initialBalance)
                .status(status)
                .customerId(customerId)
                .customerName(customerName)
                .build();
        
        account.validate();
        return account;
    }

    /**
     * Domain validation rules
     */
    public void validate() {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        if (accountType == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }
        if (currentBalance == null) {
            throw new IllegalArgumentException("Current balance cannot be null");
        }
        if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Current balance cannot be negative");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
    }

    /**
     * Business method to process credit (deposit)
     */
    public Account credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be greater than zero");
        }
        
        BigDecimal newBalance = this.currentBalance.add(amount);
        
        return Account.builder()
                .accountId(this.accountId)
                .accountNumber(this.accountNumber)
                .accountType(this.accountType)
                .currentBalance(newBalance)
                .status(this.status)
                .customerId(this.customerId)
                .customerName(this.customerName)
                .build();
    }

    /**
     * Business method to process debit (withdrawal)
     */
    public Account debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be greater than zero");
        }
        
        if (this.currentBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Saldo no disponible");
        }
        
        BigDecimal newBalance = this.currentBalance.subtract(amount);
        
        return Account.builder()
                .accountId(this.accountId)
                .accountNumber(this.accountNumber)
                .accountType(this.accountType)
                .currentBalance(newBalance)
                .status(this.status)
                .customerId(this.customerId)
                .customerName(this.customerName)
                .build();
    }

    /**
     * Business method to update account
     */
    public Account update(String accountNumber, AccountType accountType, boolean status) {
        return Account.builder()
                .accountId(this.accountId)
                .accountNumber(accountNumber)
                .accountType(accountType)
                .currentBalance(this.currentBalance)
                .status(status)
                .customerId(this.customerId)
                .customerName(this.customerName)
                .build();
    }

    /**
     * Factory method to reconstitute an account from persistence
     */
    public static Account reconstitute(AccountId accountId, String accountNumber, 
                                      AccountType accountType, BigDecimal currentBalance, 
                                      boolean status, CustomerId customerId, String customerName) {
        return Account.builder()
                .accountId(accountId)
                .accountNumber(accountNumber)
                .accountType(accountType)
                .currentBalance(currentBalance)
                .status(status)
                .customerId(customerId)
                .customerName(customerName)
                .build();
    }
}
