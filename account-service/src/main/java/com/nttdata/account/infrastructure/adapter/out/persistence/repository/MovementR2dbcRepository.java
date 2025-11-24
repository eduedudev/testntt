package com.nttdata.account.infrastructure.adapter.out.persistence.repository;

import com.nttdata.account.infrastructure.adapter.out.persistence.entity.MovementEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * R2DBC Repository for Movement entity
 */
@Repository
public interface MovementR2dbcRepository extends ReactiveCrudRepository<MovementEntity, UUID> {
    
    Flux<MovementEntity> findByAccountId(UUID accountId);
    
    @Query("SELECT * FROM movements WHERE account_id = :accountId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    Flux<MovementEntity> findByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);
}
