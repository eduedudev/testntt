package com.nttdata.account.domain.port.out;

import com.nttdata.account.domain.model.AccountId;
import com.nttdata.account.domain.model.Movement;
import com.nttdata.account.domain.model.MovementId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Output port for Movement persistence operations
 */
public interface MovementRepository {
    
    /**
     * Save a movement
     */
    Mono<Movement> save(Movement movement);
    
    /**
     * Find a movement by ID
     */
    Mono<Movement> findById(MovementId movementId);
    
    /**
     * Find all movements
     */
    Flux<Movement> findAll();
    
    /**
     * Find movements by account ID
     */
    Flux<Movement> findByAccountId(AccountId accountId);
    
    /**
     * Find movements by account ID and date range
     */
    Flux<Movement> findByAccountIdAndDateBetween(
            AccountId accountId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Delete a movement by ID
     */
    Mono<Void> deleteById(MovementId movementId);
}
