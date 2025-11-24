package com.nttdata.account.application.service;

import com.nttdata.account.application.usecase.MovementUseCase;
import com.nttdata.account.domain.exception.AccountNotFoundException;
import com.nttdata.account.domain.exception.MovementNotFoundException;
import com.nttdata.account.domain.model.*;
import com.nttdata.account.domain.port.out.AccountRepository;
import com.nttdata.account.domain.port.out.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Movement Service - Application layer
 * Implements business logic for account movements
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MovementService implements MovementUseCase {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Mono<Movement> createMovement(Movement movement) {
        log.info("Processing movement for account: {}", movement.getAccountId());
        
        return accountRepository.findById(movement.getAccountId())
                .switchIfEmpty(Mono.error(new AccountNotFoundException(
                        "Account not found with ID: " + movement.getAccountId())))
                .flatMap(account -> {
                    // Validate movement
                    movement.validate();
                    
                    // Process the movement based on type
                    Account updatedAccount;
                    if (movement.getMovementType() == MovementType.CREDIT) {
                        updatedAccount = account.credit(movement.getValue());
                    } else {
                        updatedAccount = account.debit(movement.getValue());
                    }
                    
                    // Create movement with updated balance
                    Movement finalMovement = Movement.builder()
                            .movementId(new MovementId(java.util.UUID.randomUUID()))
                            .date(java.time.LocalDateTime.now())
                            .movementType(movement.getMovementType())
                            .value(movement.getValue())
                            .balance(updatedAccount.getCurrentBalance())
                            .accountId(movement.getAccountId())
                            .accountNumber(updatedAccount.getAccountNumber())
                            .build();
                    
                    // Save account and movement
                    return accountRepository.save(updatedAccount)
                            .then(movementRepository.save(finalMovement));
                })
                .doOnSuccess(savedMovement -> 
                    log.info("Movement processed successfully: {}", savedMovement.getMovementId()));
    }

    @Override
    public Mono<Movement> getMovementById(MovementId movementId) {
        log.info("Fetching movement with ID: {}", movementId);
        
        return movementRepository.findById(movementId)
                .switchIfEmpty(Mono.error(new MovementNotFoundException(
                        "Movement not found with ID: " + movementId)))
                .doOnSuccess(movement -> 
                    log.info("Movement found with ID: {}", movementId));
    }

    @Override
    public Flux<Movement> getAllMovements() {
        log.info("Fetching all movements");
        
        return movementRepository.findAll()
                .doOnComplete(() -> log.info("All movements fetched successfully"));
    }

    @Override
    public Mono<Movement> updateMovement(MovementId movementId, Movement movement) {
        log.info("Updating movement with ID: {}", movementId);
        
        return movementRepository.findById(movementId)
                .switchIfEmpty(Mono.error(new MovementNotFoundException(
                        "Movement not found with ID: " + movementId)))
                .flatMap(existingMovement -> {
                    // Note: Updating movements is complex and may require reverting
                    // the original transaction and applying a new one
                    // For simplicity, we'll allow updating but this should be carefully
                    // considered in production
                    Movement updatedMovement = Movement.builder()
                            .movementId(movementId)
                            .date(movement.getDate())
                            .movementType(movement.getMovementType())
                            .value(movement.getValue())
                            .balance(movement.getBalance())
                            .accountId(existingMovement.getAccountId())
                            .accountNumber(existingMovement.getAccountNumber())
                            .build();
                    
                    updatedMovement.validate();
                    return movementRepository.save(updatedMovement);
                })
                .doOnSuccess(updatedMovement -> 
                    log.info("Movement updated successfully with ID: {}", movementId));
    }

    @Override
    public Mono<Void> deleteMovement(MovementId movementId) {
        log.info("Deleting movement with ID: {}", movementId);
        
        return movementRepository.findById(movementId)
                .switchIfEmpty(Mono.error(new MovementNotFoundException(
                        "Movement not found with ID: " + movementId)))
                .flatMap(movement -> movementRepository.deleteById(movementId))
                .doOnSuccess(v -> 
                    log.info("Movement deleted successfully with ID: {}", movementId));
    }
}
