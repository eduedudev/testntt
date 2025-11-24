package com.nttdata.customer.infrastructure.adapter.out.persistence;

import com.nttdata.customer.domain.model.*;
import com.nttdata.customer.domain.port.out.CustomerRepository;
import com.nttdata.customer.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import com.nttdata.customer.infrastructure.adapter.out.persistence.repository.CustomerR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter implementation for Customer persistence
 * Implements the output port defined in the domain layer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerR2dbcRepository r2dbcRepository;

    @Override
    public Mono<Customer> save(Customer customer) {
        CustomerEntity entity = toEntity(customer);
        return r2dbcRepository.save(entity)
                .map(this::toDomain)
                .doOnSuccess(c -> log.debug("Customer saved: {}", c.getCustomerId()));
    }

    @Override
    public Mono<Customer> findById(CustomerId customerId) {
        return r2dbcRepository.findById(customerId.getValue())
                .map(this::toDomain)
                .doOnSuccess(c -> log.debug("Customer found: {}", customerId));
    }

    @Override
    public Mono<Customer> findByIdentification(String identification) {
        return r2dbcRepository.findByIdentification(identification)
                .map(this::toDomain)
                .doOnSuccess(c -> log.debug("Customer found by identification: {}", identification));
    }

    @Override
    public Flux<Customer> findAll() {
        return r2dbcRepository.findAll()
                .map(this::toDomain)
                .doOnComplete(() -> log.debug("All customers fetched"));
    }

    @Override
    public Mono<Void> deleteById(CustomerId customerId) {
        return r2dbcRepository.deleteById(customerId.getValue())
                .doOnSuccess(v -> log.debug("Customer deleted: {}", customerId));
    }

    @Override
    public Mono<Boolean> existsByIdentification(String identification) {
        return r2dbcRepository.existsByIdentification(identification)
                .doOnSuccess(exists -> log.debug("Customer exists by identification {}: {}", 
                        identification, exists));
    }

    /**
     * Map domain model to entity
     */
    private CustomerEntity toEntity(Customer customer) {
        return CustomerEntity.builder()
                .customerId(customer.getCustomerId().getValue())
                .name(customer.getPerson().getName())
                .gender(customer.getPerson().getGender().name())
                .identification(customer.getPerson().getIdentification())
                .address(customer.getPerson().getAddress().getValue())
                .phone(customer.getPerson().getPhone().getNumber())
                .password(customer.getPassword())
                .status(customer.isStatus())
                .isNew(true)
                .build();
    }

    /**
     * Map entity to domain model
     */
    private Customer toDomain(CustomerEntity entity) {
        Person person = Person.builder()
                .name(entity.getName())
                .gender(Gender.valueOf(entity.getGender()))
                .identification(entity.getIdentification())
                .address(new Address(entity.getAddress()))
                .phone(new Phone(entity.getPhone()))
                .build();

        return Customer.builder()
                .customerId(new CustomerId(entity.getCustomerId()))
                .person(person)
                .password(entity.getPassword())
                .status(entity.getStatus())
                .build();
    }
}
