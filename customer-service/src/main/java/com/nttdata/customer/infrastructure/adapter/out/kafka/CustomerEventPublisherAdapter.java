package com.nttdata.customer.infrastructure.adapter.out.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.customer.domain.model.CustomerId;
import com.nttdata.customer.domain.port.out.CustomerEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Kafka adapter for publishing customer events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventPublisherAdapter implements CustomerEventPublisher {

    private final ReactiveKafkaProducerTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.customer-events}")
    private String customerEventsTopic;

    @Override
    public Mono<Void> publishCustomerCreated(CustomerId customerId, String customerName) {
        return publishEvent(buildEvent("CUSTOMER_CREATED", customerId, customerName));
    }

    @Override
    public Mono<Void> publishCustomerUpdated(CustomerId customerId, String customerName) {
        return publishEvent(buildEvent("CUSTOMER_UPDATED", customerId, customerName));
    }

    @Override
    public Mono<Void> publishCustomerDeleted(CustomerId customerId) {
        return publishEvent(buildEvent("CUSTOMER_DELETED", customerId, null));
    }

    private CustomerEvent buildEvent(String eventType, CustomerId customerId, String customerName) {
        return CustomerEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .customerId(customerId.toString())
                .customerName(customerName)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private Mono<Void> publishEvent(CustomerEvent event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            log.info("Publishing event: {} to topic: {}", event.getEventType(), customerEventsTopic);
            
            return kafkaTemplate.send(customerEventsTopic, event.getCustomerId(), jsonEvent)
                    .doOnSuccess(result -> log.info("Event published successfully: {}", event.getEventType()))
                    .doOnError(error -> log.error("Error publishing event: {}", event.getEventType(), error))
                    .then();
        } catch (JsonProcessingException e) {
            log.error("Error serializing event", e);
            return Mono.error(e);
        }
    }
}
