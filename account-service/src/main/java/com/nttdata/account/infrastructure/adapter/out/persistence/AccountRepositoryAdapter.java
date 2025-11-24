package com.nttdata.account.infrastructure.adapter.out.persistence;

import com.nttdata.account.domain.model.Account;
import com.nttdata.account.domain.model.AccountId;
import com.nttdata.account.domain.model.CustomerId;
import com.nttdata.account.domain.port.out.AccountRepository;
import com.nttdata.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import com.nttdata.account.infrastructure.adapter.out.persistence.repository.AccountR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountR2dbcRepository r2dbcRepository;

    @Override
    public Mono<Account> save(Account account) {
        // Check if account exists to determine if it's new or update
        return r2dbcRepository.existsById(account.getAccountId().getValue())
                .flatMap(exists -> {
                    AccountEntity entity = toEntity(account, !exists);
                    return r2dbcRepository.save(entity);
                })
                .map(this::toDomain);
    }

    @Override
    public Mono<Account> findById(AccountId id) {
        return r2dbcRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public Mono<Account> findByAccountNumber(String accountNumber) {
        return r2dbcRepository.findByAccountNumber(accountNumber)
                .map(this::toDomain);
    }

    @Override
    public Flux<Account> findAll() {
        return r2dbcRepository.findAll()
                .map(this::toDomain);
    }

    @Override
    public Flux<Account> findByCustomerId(CustomerId customerId) {
        return r2dbcRepository.findByCustomerId(customerId.getValue())
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(AccountId id) {
        return r2dbcRepository.deleteById(id.getValue());
    }

    @Override
    public Mono<Boolean> existsByAccountNumber(String accountNumber) {
        return r2dbcRepository.existsByAccountNumber(accountNumber);
    }

    private AccountEntity toEntity(Account account, boolean isNew) {
        return AccountEntity.builder()
                .accountId(account.getAccountId().getValue())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .currentBalance(account.getCurrentBalance())
                .status(account.isStatus())
                .customerId(account.getCustomerId().getValue())
                .customerName(account.getCustomerName())
                .isNew(isNew)  // ← Marca como nueva solo si no existe en BD
                .build();
    }

    private Account toDomain(AccountEntity entity) {
        return Account.reconstitute(
                new AccountId(entity.getAccountId()),
                entity.getAccountNumber(),
                com.nttdata.account.domain.model.AccountType.valueOf(entity.getAccountType()),
                entity.getCurrentBalance(),
                entity.getStatus(),
                new CustomerId(entity.getCustomerId()),
                entity.getCustomerName()
        );
    }
}
