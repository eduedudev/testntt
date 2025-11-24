package com.nttdata.customer.infrastructure.adapter.out.persistence.repository;

import com.nttdata.customer.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * R2DBC Repository for Customer entity
 */
@Repository
public interface CustomerR2dbcRepository extends ReactiveCrudRepository<CustomerEntity, UUID> {
    
    Mono<CustomerEntity> findByIdentification(String identification);
    
    Mono<Boolean> existsByIdentification(String identification);
}
