package com.nttdata.customer.infrastructure.adapter.in.rest;

import com.nttdata.customer.application.usecase.CustomerUseCase;
import com.nttdata.customer.domain.model.CustomerId;
import com.nttdata.customer.infrastructure.adapter.in.rest.api.CustomersApi;
import com.nttdata.customer.infrastructure.adapter.in.rest.dto.CustomerRequest;
import com.nttdata.customer.infrastructure.adapter.in.rest.dto.CustomerResponse;
import com.nttdata.customer.infrastructure.adapter.in.rest.mapper.CustomerDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * REST Controller for Customer operations
 * Implements the OpenAPI generated interface
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerUseCase customerUseCase;
    private final CustomerDtoMapper mapper;

    @Override
    public Mono<ResponseEntity<Flux<CustomerResponse>>> getAllCustomers(ServerWebExchange exchange) {
        log.info("REST request to get all customers");
        
        Flux<CustomerResponse> customers = customerUseCase.getAllCustomers()
                .map(mapper::toResponse);
        
        return Mono.just(ResponseEntity.ok(customers));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> createCustomer(
            Mono<CustomerRequest> customerRequest, 
            ServerWebExchange exchange) {
        log.info("REST request to create customer");
        
        return customerRequest
                .map(mapper::toDomain)
                .flatMap(customerUseCase::createCustomer)
                .map(mapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> getCustomerById(
            UUID id, 
            ServerWebExchange exchange) {
        log.info("REST request to get customer by id: {}", id);
        
        return customerUseCase.getCustomerById(new CustomerId(id))
                .map(mapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> updateCustomer(
            UUID id, 
            Mono<CustomerRequest> customerRequest, 
            ServerWebExchange exchange) {
        log.info("REST request to update customer with id: {}", id);
        
        return customerRequest
                .map(request -> mapper.toDomain(id, request))
                .flatMap(customer -> customerUseCase.updateCustomer(new CustomerId(id), customer))
                .map(mapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomer(UUID id, ServerWebExchange exchange) {
        log.info("REST request to delete customer with id: {}", id);
        
        return customerUseCase.deleteCustomer(new CustomerId(id))
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
