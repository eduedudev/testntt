package com.nttdata.account.infrastructure.adapter.out.persistence;

import com.nttdata.account.domain.model.CustomerId;
import com.nttdata.account.domain.port.out.CustomerInfoRepository;
import com.nttdata.account.infrastructure.adapter.out.persistence.entity.CustomerInfoEntity;
import com.nttdata.account.infrastructure.adapter.out.persistence.repository.CustomerInfoR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerInfoRepositoryAdapter implements CustomerInfoRepository {

    private final CustomerInfoR2dbcRepository r2dbcRepository;

    @Override
    public Mono<String> findCustomerNameById(CustomerId customerId) {
        return r2dbcRepository.findById(customerId.getValue())
                .map(entity -> entity.getCustomerName());
    }

    @Override
    public Mono<Void> saveCustomerInfo(CustomerId customerId, String customerName) {
        // Create entity and save
        return r2dbcRepository.findById(customerId.getValue())
                .flatMap(existing -> {
                    existing.setCustomerName(customerName);
                    return r2dbcRepository.save(existing);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    CustomerInfoEntity entity = new CustomerInfoEntity();
                    entity.setCustomerId(customerId.getValue());
                    entity.setCustomerName(customerName);
                    return r2dbcRepository.save(entity);
                }))
                .then();
    }

    @Override
    public Mono<Void> deleteCustomerInfo(CustomerId customerId) {
        return r2dbcRepository.deleteById(customerId.getValue());
    }
}
