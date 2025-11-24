package com.nttdata.account.application.usecase;

import com.nttdata.account.domain.model.Movement;
import com.nttdata.account.domain.model.MovementId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Input port - Use cases for Movement management
 */
public interface MovementUseCase {
    
    /**
     * Create a new movement (processes transaction on account)
     */
    Mono<Movement> createMovement(Movement movement);
    
    /**
     * Get movement by ID
     */
    Mono<Movement> getMovementById(MovementId movementId);
    
    /**
     * Get all movements
     */
    Flux<Movement> getAllMovements();
    
    /**
     * Update movement
     */
    Mono<Movement> updateMovement(MovementId movementId, Movement movement);
    
    /**
     * Delete movement
     */
    Mono<Void> deleteMovement(MovementId movementId);
}
