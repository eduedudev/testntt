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
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Movement entity for R2DBC persistence
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("movements")
public class MovementEntity implements Persistable<UUID> {
    
    @Id
    @Column("movement_id")
    private UUID movementId;
    
    @Transient
    @Builder.Default
    private boolean isNew = true;
    
    @Column("date")
    private LocalDateTime date;
    
    @Column("movement_type")
    private String movementType;
    
    @Column("value")
    private BigDecimal value;
    
    @Column("balance")
    private BigDecimal balance;
    
    @Column("account_id")
    private UUID accountId;
    
    @Column("account_number")
    private String accountNumber;
    
    @Override
    public UUID getId() {
        return movementId;
    }
    
    @Override
    public boolean isNew() {
        return isNew;
    }
}
