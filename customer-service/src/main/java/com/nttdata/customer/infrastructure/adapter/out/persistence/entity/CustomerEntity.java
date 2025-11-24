package com.nttdata.customer.infrastructure.adapter.out.persistence.entity;

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
 * Customer entity for R2DBC persistence
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("customers")
public class CustomerEntity implements Persistable<UUID> {
    
    @Id
    @Column("customer_id")
    private UUID customerId;
    
    @Column("name")
    private String name;
    
    @Column("gender")
    private String gender;
    
    @Column("identification")
    private String identification;
    
    @Column("address")
    private String address;
    
    @Column("phone")
    private String phone;
    
    @Column("password")
    private String password;
    
    @Column("status")
    private Boolean status;
    
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
