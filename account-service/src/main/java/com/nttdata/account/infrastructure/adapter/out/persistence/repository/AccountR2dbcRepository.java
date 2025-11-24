package com.nttdata.account.infrastructure.adapter.out.persistence.repository;

import com.nttdata.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * R2DBC Repository for Account entity
 */
@Repository
public interface AccountR2dbcRepository extends ReactiveCrudRepository<AccountEntity, UUID> {
    
    Mono<AccountEntity> findByAccountNumber(String accountNumber);
    
    Flux<AccountEntity> findByCustomerId(UUID customerId);
    
    Mono<Boolean> existsByAccountNumber(String accountNumber);
}
