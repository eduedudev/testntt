package com.nttdata.account.application.usecase;

import com.nttdata.account.domain.model.Account;
import com.nttdata.account.domain.model.AccountId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Input port - Use cases for Account management
 */
public interface AccountUseCase {
    
    /**
     * Create a new account
     */
    Mono<Account> createAccount(Account account);
    
    /**
     * Get account by ID
     */
    Mono<Account> getAccountById(AccountId accountId);
    
    /**
     * Get all accounts
     */
    Flux<Account> getAllAccounts();
    
    /**
     * Update account
     */
    Mono<Account> updateAccount(AccountId accountId, Account account);
    
    /**
     * Delete account
     */
    Mono<Void> deleteAccount(AccountId accountId);
}
