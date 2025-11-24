package com.nttdata.customer.domain.port.out;

import com.nttdata.customer.domain.model.CustomerId;
import reactor.core.publisher.Mono;

/**
 * Output port for publishing customer events to Kafka
 */
public interface CustomerEventPublisher {
    
    /**
     * Publish customer created event
     */
    Mono<Void> publishCustomerCreated(CustomerId customerId, String customerName);
    
    /**
     * Publish customer updated event
     */
    Mono<Void> publishCustomerUpdated(CustomerId customerId, String customerName);
    
    /**
     * Publish customer deleted event
     */
    Mono<Void> publishCustomerDeleted(CustomerId customerId);
}
