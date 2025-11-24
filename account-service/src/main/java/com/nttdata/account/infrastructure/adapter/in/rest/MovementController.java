package com.nttdata.account.infrastructure.adapter.in.rest;

import com.nttdata.account.application.usecase.MovementUseCase;
import com.nttdata.account.domain.model.MovementId;
import com.nttdata.account.infrastructure.adapter.in.rest.api.MovementsApi;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.MovementRequest;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.MovementResponse;
import com.nttdata.account.infrastructure.adapter.in.rest.mapper.MovementDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MovementController implements MovementsApi {

    private final MovementUseCase movementUseCase;
    private final MovementDtoMapper movementDtoMapper;

    @Override
    public Mono<ResponseEntity<MovementResponse>> createMovement(
            Mono<MovementRequest> movementRequest,
            ServerWebExchange exchange) {
        log.info("Creating new movement");
        return movementRequest
                .flatMap(request -> movementUseCase.createMovement(
                        movementDtoMapper.toDomain(request))
                )
                .map(movementDtoMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .doOnSuccess(response -> log.info("Movement created successfully: {}", 
                        response.getBody().getMovementId()))
                .doOnError(error -> log.error("Error creating movement", error));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteMovement(UUID id, ServerWebExchange exchange) {
        log.info("Deleting movement with id: {}", id);
        return movementUseCase.deleteMovement(MovementId.of(id))
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().<Void>build()))
                .doOnSuccess(response -> log.info("Movement deleted successfully: {}", id))
                .doOnError(error -> log.error("Error deleting movement: {}", id, error));
    }

    @Override
    public Mono<ResponseEntity<MovementResponse>> getMovementById(
            UUID id, 
            ServerWebExchange exchange) {
        log.info("Getting movement by id: {}", id);
        return movementUseCase.getMovementById(MovementId.of(id))
                .map(movementDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Movement retrieved: {}", id))
                .doOnError(error -> log.error("Error getting movement: {}", id, error));
    }

    @Override
    public Mono<ResponseEntity<Flux<MovementResponse>>> getAllMovements(ServerWebExchange exchange) {
        log.info("Getting all movements");
        return Mono.just(ResponseEntity.ok(
                movementUseCase.getAllMovements()
                        .map(movementDtoMapper::toResponse)
                        .doOnComplete(() -> log.info("All movements retrieved"))
                        .doOnError(error -> log.error("Error getting all movements", error))
        ));
    }

    @Override
    public Mono<ResponseEntity<MovementResponse>> updateMovement(
            UUID id,
            Mono<MovementRequest> movementRequest,
            ServerWebExchange exchange) {
        log.info("Updating movement with id: {}", id);
        return movementRequest
                .flatMap(request -> movementUseCase.updateMovement(
                        MovementId.of(id), 
                        movementDtoMapper.toDomain(request))
                )
                .map(movementDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Movement updated successfully: {}", id))
                .doOnError(error -> log.error("Error updating movement: {}", id, error));
    }
}
