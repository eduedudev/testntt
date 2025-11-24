package com.nttdata.account.domain.port.out;

import com.nttdata.account.domain.model.CustomerId;
import reactor.core.publisher.Mono;

/**
 * Output port for Customer information (from Kafka events)
 */
public interface CustomerInfoRepository {
    
    /**
     * Find customer name by ID
     */
    Mono<String> findCustomerNameById(CustomerId customerId);
    
    /**
     * Save or update customer info
     */
    Mono<Void> saveCustomerInfo(CustomerId customerId, String customerName);
    
    /**
     * Delete customer info
     */
    Mono<Void> deleteCustomerInfo(CustomerId customerId);
}
