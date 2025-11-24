package com.nttdata.account.domain.port.out;

import com.nttdata.account.domain.model.Account;
import com.nttdata.account.domain.model.AccountId;
import com.nttdata.account.domain.model.CustomerId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Output port for Account persistence operations
 */
public interface AccountRepository {
    
    /**
     * Save an account
     */
    Mono<Account> save(Account account);
    
    /**
     * Find an account by ID
     */
    Mono<Account> findById(AccountId accountId);
    
    /**
     * Find an account by account number
     */
    Mono<Account> findByAccountNumber(String accountNumber);
    
    /**
     * Find all accounts
     */
    Flux<Account> findAll();
    
    /**
     * Find accounts by customer ID
     */
    Flux<Account> findByCustomerId(CustomerId customerId);
    
    /**
     * Delete an account by ID
     */
    Mono<Void> deleteById(AccountId accountId);
    
    /**
     * Check if an account exists by account number
     */
    Mono<Boolean> existsByAccountNumber(String accountNumber);
}
