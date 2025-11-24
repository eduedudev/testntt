package com.nttdata.customer.application.service;

import com.nttdata.customer.domain.exception.CustomerAlreadyExistsException;
import com.nttdata.customer.domain.exception.CustomerNotFoundException;
import com.nttdata.customer.domain.model.*;
import com.nttdata.customer.domain.port.out.CustomerEventPublisher;
import com.nttdata.customer.domain.port.out.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerEventPublisher eventPublisher;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private CustomerId testCustomerId;

    @BeforeEach
    void setUp() {
        testCustomerId = new CustomerId(UUID.randomUUID());
        
        Person person = Person.builder()
                .name("Jose Lema")
                .gender(Gender.MALE)
                .identification("1234567890")
                .address(new Address("Otavalo sn y principal"))
                .phone(new Phone("098254785"))
                .build();

        testCustomer = Customer.builder()
                .customerId(testCustomerId)
                .person(person)
                .password("1234")
                .status(true)
                .build();
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() {
        // Given
        when(customerRepository.existsByIdentification(any())).thenReturn(Mono.just(false));
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(testCustomer));
        when(eventPublisher.publishCustomerCreated(any(), any())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(customerService.createCustomer(testCustomer))
                .expectNext(testCustomer)
                .verifyComplete();

        verify(customerRepository).existsByIdentification("1234567890");
        verify(customerRepository).save(any(Customer.class));
        verify(eventPublisher).publishCustomerCreated(testCustomerId, "Jose Lema");
    }

    @Test
    @DisplayName("Should throw exception when customer already exists")
    void shouldThrowExceptionWhenCustomerAlreadyExists() {
        // Given
        when(customerRepository.existsByIdentification(any())).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(customerService.createCustomer(testCustomer))
                .expectError(CustomerAlreadyExistsException.class)
                .verify();

        verify(customerRepository).existsByIdentification("1234567890");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should get customer by id successfully")
    void shouldGetCustomerByIdSuccessfully() {
        // Given
        when(customerRepository.findById(testCustomerId)).thenReturn(Mono.just(testCustomer));

        // When & Then
        StepVerifier.create(customerService.getCustomerById(testCustomerId))
                .expectNext(testCustomer)
                .verifyComplete();

        verify(customerRepository).findById(testCustomerId);
    }

    @Test
    @DisplayName("Should throw exception when customer not found by id")
    void shouldThrowExceptionWhenCustomerNotFoundById() {
        // Given
        when(customerRepository.findById(testCustomerId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(customerService.getCustomerById(testCustomerId))
                .expectError(CustomerNotFoundException.class)
                .verify();

        verify(customerRepository).findById(testCustomerId);
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void shouldGetAllCustomersSuccessfully() {
        // Given
        when(customerRepository.findAll()).thenReturn(Flux.just(testCustomer));

        // When & Then
        StepVerifier.create(customerService.getAllCustomers())
                .expectNext(testCustomer)
                .verifyComplete();

        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        // Given
        Person updatedPerson = Person.builder()
                .name("Jose Lema Updated")
                .gender(Gender.MALE)
                .identification("1234567890")
                .address(new Address("New Address"))
                .phone(new Phone("098254785"))
                .build();

        Customer updatedCustomer = Customer.builder()
                .customerId(testCustomerId)
                .person(updatedPerson)
                .password("newpassword")
                .status(true)
                .build();

        when(customerRepository.findById(testCustomerId)).thenReturn(Mono.just(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(updatedCustomer));
        when(eventPublisher.publishCustomerUpdated(any(), any())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(customerService.updateCustomer(testCustomerId, updatedCustomer))
                .expectNext(updatedCustomer)
                .verifyComplete();

        verify(customerRepository).findById(testCustomerId);
        verify(customerRepository).save(any(Customer.class));
        verify(eventPublisher).publishCustomerUpdated(testCustomerId, "Jose Lema Updated");
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() {
        // Given
        when(customerRepository.findById(testCustomerId)).thenReturn(Mono.just(testCustomer));
        when(customerRepository.deleteById(testCustomerId)).thenReturn(Mono.empty());
        when(eventPublisher.publishCustomerDeleted(testCustomerId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(customerService.deleteCustomer(testCustomerId))
                .verifyComplete();

        verify(customerRepository).findById(testCustomerId);
        verify(customerRepository).deleteById(testCustomerId);
        verify(eventPublisher).publishCustomerDeleted(testCustomerId);
    }
}
