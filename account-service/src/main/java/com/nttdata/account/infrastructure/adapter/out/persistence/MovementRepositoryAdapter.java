package com.nttdata.account.infrastructure.adapter.out.persistence;

import com.nttdata.account.domain.model.AccountId;
import com.nttdata.account.domain.model.Movement;
import com.nttdata.account.domain.model.MovementId;
import com.nttdata.account.domain.port.out.MovementRepository;
import com.nttdata.account.infrastructure.adapter.out.persistence.entity.MovementEntity;
import com.nttdata.account.infrastructure.adapter.out.persistence.repository.MovementR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MovementRepositoryAdapter implements MovementRepository {

    private final MovementR2dbcRepository r2dbcRepository;

    @Override
    public Mono<Movement> save(Movement movement) {
        MovementEntity entity = toEntity(movement);
        return r2dbcRepository.save(entity)
                .map(this::toDomain);
    }

    @Override
    public Mono<Movement> findById(MovementId id) {
        return r2dbcRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public Flux<Movement> findAll() {
        return r2dbcRepository.findAll()
                .map(this::toDomain);
    }

    @Override
    public Flux<Movement> findByAccountId(AccountId accountId) {
        return r2dbcRepository.findByAccountId(accountId.getValue())
                .map(this::toDomain);
    }

    @Override
    public Flux<Movement> findByAccountIdAndDateBetween(AccountId accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return r2dbcRepository.findByAccountIdAndDateBetween(
                        accountId.getValue(),
                        startDate,
                        endDate
                )
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(MovementId id) {
        return r2dbcRepository.deleteById(id.getValue());
    }

    private MovementEntity toEntity(Movement movement) {
        return MovementEntity.builder()
                .movementId(movement.getMovementId().getValue())
                .date(movement.getDate())
                .movementType(movement.getMovementType().name())
                .value(movement.getValue())
                .balance(movement.getBalance())
                .accountId(movement.getAccountId().getValue())
                .accountNumber(movement.getAccountNumber())
                .isNew(true)  // Siempre es nuevo cuando se crea desde el dominio
                .build();
    }

    private Movement toDomain(MovementEntity entity) {
        return Movement.reconstitute(
                new MovementId(entity.getMovementId()),
                entity.getDate(),
                com.nttdata.account.domain.model.MovementType.valueOf(entity.getMovementType()),
                entity.getValue(),
                entity.getBalance(),
                new AccountId(entity.getAccountId()),
                entity.getAccountNumber()
        );
    }
}
