package com.nttdata.account.infrastructure.adapter.in.rest;

import com.nttdata.account.application.usecase.AccountUseCase;
import com.nttdata.account.domain.model.AccountId;
import com.nttdata.account.infrastructure.adapter.in.rest.api.AccountsApi;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.AccountRequest;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.AccountResponse;
import com.nttdata.account.infrastructure.adapter.in.rest.mapper.AccountDtoMapper;
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
public class AccountController implements AccountsApi {

    private final AccountUseCase accountUseCase;
    private final AccountDtoMapper accountDtoMapper;

    @Override
    public Mono<ResponseEntity<AccountResponse>> createAccount(
            Mono<AccountRequest> accountRequest,
            ServerWebExchange exchange) {
        log.info("Creating new account");
        return accountRequest
                .flatMap(request -> accountUseCase.createAccount(
                        accountDtoMapper.toDomain(request))
                )
                .map(accountDtoMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .doOnSuccess(response -> log.info("Account created successfully: {}", 
                        response.getBody().getAccountId()))
                .doOnError(error -> log.error("Error creating account", error));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(UUID id, ServerWebExchange exchange) {
        log.info("Deleting account with id: {}", id);
        return accountUseCase.deleteAccount(AccountId.of(id))
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().<Void>build()))
                .doOnSuccess(response -> log.info("Account deleted successfully: {}", id))
                .doOnError(error -> log.error("Error deleting account: {}", id, error));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> getAccountById(
            UUID id, 
            ServerWebExchange exchange) {
        log.info("Getting account by id: {}", id);
        return accountUseCase.getAccountById(AccountId.of(id))
                .map(accountDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Account retrieved: {}", id))
                .doOnError(error -> log.error("Error getting account: {}", id, error));
    }

    @Override
    public Mono<ResponseEntity<Flux<AccountResponse>>> getAllAccounts(ServerWebExchange exchange) {
        log.info("Getting all accounts");
        return Mono.just(ResponseEntity.ok(
                accountUseCase.getAllAccounts()
                        .map(accountDtoMapper::toResponse)
                        .doOnComplete(() -> log.info("All accounts retrieved"))
                        .doOnError(error -> log.error("Error getting all accounts", error))
        ));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> updateAccount(
            UUID id,
            Mono<AccountRequest> accountRequest,
            ServerWebExchange exchange) {
        log.info("Updating account with id: {}", id);
        return accountRequest
                .flatMap(request -> accountUseCase.updateAccount(
                        AccountId.of(id), 
                        accountDtoMapper.toDomain(request))
                )
                .map(accountDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Account updated successfully: {}", id))
                .doOnError(error -> log.error("Error updating account: {}", id, error));
    }
}
