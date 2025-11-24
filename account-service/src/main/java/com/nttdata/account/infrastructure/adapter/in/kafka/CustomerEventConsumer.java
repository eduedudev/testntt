package com.nttdata.account.infrastructure.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.account.infrastructure.adapter.out.persistence.entity.CustomerInfoEntity;
import com.nttdata.account.infrastructure.adapter.out.persistence.repository.CustomerInfoR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Kafka consumer for customer events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventConsumer {

    private final ReactiveKafkaConsumerTemplate<String, String> kafkaConsumer;
    private final CustomerInfoR2dbcRepository customerInfoRepository;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.customer-events}")
    private String customerEventsTopic;

    @EventListener(ApplicationReadyEvent.class)
    public void startConsuming() {
        log.info("Starting Kafka consumer for topic: {}", customerEventsTopic);
        
        kafkaConsumer
                .receiveAutoAck()
                .doOnNext(record -> log.info("Received message: key={}, value={}", 
                        record.key(), record.value()))
                .flatMap(record -> processEvent(record.value()))
                .doOnError(error -> log.error("Error processing Kafka message", error))
                .retry()
                .subscribe();
    }

    private Mono<Void> processEvent(String eventJson) {
        try {
            CustomerEvent event = objectMapper.readValue(eventJson, CustomerEvent.class);
            log.info("Processing event: type={}, customerId={}", 
                    event.getEventType(), event.getCustomerId());
            
            return switch (event.getEventType()) {
                case "CUSTOMER_CREATED", "CUSTOMER_UPDATED" -> upsertCustomerInfo(event);
                case "CUSTOMER_DELETED" -> deleteCustomerInfo(event);
                default -> {
                    log.warn("Unknown event type: {}", event.getEventType());
                    yield Mono.empty();
                }
            };
        } catch (Exception e) {
            log.error("Error parsing event JSON", e);
            return Mono.error(e);
        }
    }

    private Mono<Void> upsertCustomerInfo(CustomerEvent event) {
        CustomerInfoEntity entity = CustomerInfoEntity.builder()
                .customerId(UUID.fromString(event.getCustomerId()))
                .customerName(event.getCustomerName())
                .isNew(true)
                .build();
        
        return customerInfoRepository.save(entity)
                .doOnSuccess(saved -> log.info("Customer info saved: {}", saved.getCustomerId()))
                .then();
    }

    private Mono<Void> deleteCustomerInfo(CustomerEvent event) {
        return customerInfoRepository.deleteById(UUID.fromString(event.getCustomerId()))
                .doOnSuccess(v -> log.info("Customer info deleted: {}", event.getCustomerId()));
    }
}
