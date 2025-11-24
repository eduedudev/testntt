package com.nttdata.account.infrastructure.adapter.in.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Customer event DTO for Kafka messages
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEvent {
    private String eventId;
    private String eventType;
    private String customerId;
    private String customerName;
    private LocalDateTime timestamp;
}
