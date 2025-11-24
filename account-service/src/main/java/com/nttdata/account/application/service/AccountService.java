package com.nttdata.account.application.service;

import com.nttdata.account.application.usecase.AccountUseCase;
import com.nttdata.account.domain.exception.AccountNotFoundException;
import com.nttdata.account.domain.exception.CustomerNotFoundException;
import com.nttdata.account.domain.model.Account;
import com.nttdata.account.domain.model.AccountId;
import com.nttdata.account.domain.port.out.AccountRepository;
import com.nttdata.account.domain.port.out.CustomerInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Account Service - Application layer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements AccountUseCase {

    private final AccountRepository accountRepository;
    private final CustomerInfoRepository customerInfoRepository;

    @Override
    public Mono<Account> createAccount(Account account) {
        log.info("Creating account with number: {}", account.getAccountNumber());
        
        return customerInfoRepository.findCustomerNameById(account.getCustomerId())
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with ID: " + account.getCustomerId())))
                .flatMap(customerName -> {
                    account.validate();
                    return accountRepository.save(account);
                })
                .doOnSuccess(savedAccount -> 
                    log.info("Account created successfully with ID: {}", savedAccount.getAccountId()));
    }

    @Override
    public Mono<Account> getAccountById(AccountId accountId) {
        log.info("Fetching account with ID: {}", accountId);
        
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(
                        "Account not found with ID: " + accountId)))
                .doOnSuccess(account -> 
                    log.info("Account found with ID: {}", accountId));
    }

    @Override
    public Flux<Account> getAllAccounts() {
        log.info("Fetching all accounts");
        
        return accountRepository.findAll()
                .doOnComplete(() -> log.info("All accounts fetched successfully"));
    }

    @Override
    public Mono<Account> updateAccount(AccountId accountId, Account account) {
        log.info("Updating account with ID: {}", accountId);
        
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(
                        "Account not found with ID: " + accountId)))
                .flatMap(existingAccount -> {
                    Account updatedAccount = Account.builder()
                            .accountId(accountId)
                            .accountNumber(account.getAccountNumber())
                            .accountType(account.getAccountType())
                            .currentBalance(existingAccount.getCurrentBalance())
                            .status(account.isStatus())
                            .customerId(existingAccount.getCustomerId())
                            .customerName(existingAccount.getCustomerName())
                            .build();
                    
                    updatedAccount.validate();
                    return accountRepository.save(updatedAccount);
                })
                .doOnSuccess(updatedAccount -> 
                    log.info("Account updated successfully with ID: {}", accountId));
    }

    @Override
    public Mono<Void> deleteAccount(AccountId accountId) {
        log.info("Deleting account with ID: {}", accountId);
        
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(
                        "Account not found with ID: " + accountId)))
                .flatMap(account -> accountRepository.deleteById(accountId))
                .doOnSuccess(v -> 
                    log.info("Account deleted successfully with ID: {}", accountId));
    }
}
