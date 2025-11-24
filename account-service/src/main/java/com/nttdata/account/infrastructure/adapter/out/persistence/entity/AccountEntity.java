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

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Account entity for R2DBC persistence
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("accounts")
public class AccountEntity implements Persistable<UUID> {
    
    @Id
    @Column("account_id")
    private UUID accountId;
    
    @Transient
    @Builder.Default
    private boolean isNew = false;  // false porque las cuentas ya existen en BD
    
    @Column("account_number")
    private String accountNumber;
    
    @Column("account_type")
    private String accountType;
    
    @Column("current_balance")
    private BigDecimal currentBalance;
    
    @Column("status")
    private Boolean status;
    
    @Column("customer_id")
    private UUID customerId;
    
    @Column("customer_name")
    private String customerName;
    
    @Override
    public UUID getId() {
        return accountId;
    }
    
    @Override
    public boolean isNew() {
        return isNew;
    }
}
