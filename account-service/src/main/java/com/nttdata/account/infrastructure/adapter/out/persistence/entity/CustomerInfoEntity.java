package com.nttdata.account.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * Customer Info entity for R2DBC persistence
 * Stores customer information received from Kafka events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("customer_info")
public class CustomerInfoEntity implements Persistable<UUID> {
    
    @Id
    @Column("customer_id")
    private UUID customerId;
    
    @Column("customer_name")
    private String customerName;
    
    @Transient
    private boolean isNew;
    
    @Override
    public UUID getId() {
        return customerId;
    }
    
    @Override
    public boolean isNew() {
        return isNew;
    }
}
