package com.nttdata.account.infrastructure.adapter.out.persistence.repository;

import com.nttdata.account.infrastructure.adapter.out.persistence.entity.CustomerInfoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * R2DBC Repository for CustomerInfo entity
 */
@Repository
public interface CustomerInfoR2dbcRepository extends ReactiveCrudRepository<CustomerInfoEntity, UUID> {
}
