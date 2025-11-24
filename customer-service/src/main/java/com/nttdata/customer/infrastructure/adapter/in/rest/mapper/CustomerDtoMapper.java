package com.nttdata.customer.infrastructure.adapter.in.rest.mapper;

import com.nttdata.customer.domain.model.*;
import com.nttdata.customer.infrastructure.adapter.in.rest.dto.CustomerRequest;
import com.nttdata.customer.infrastructure.adapter.in.rest.dto.CustomerResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper between REST DTOs and Domain models
 */
@Component
public class CustomerDtoMapper {

    public Customer toDomain(CustomerRequest request) {
        Person person = Person.builder()
                .name(request.getName())
                .gender(Gender.valueOf(request.getGender().name()))
                .identification(request.getIdentification())
                .address(new Address(request.getAddress()))
                .phone(new Phone(request.getPhone()))
                .build();

        return Customer.builder()
                .customerId(new CustomerId(UUID.randomUUID()))
                .person(person)
                .password(request.getPassword())
                .status(request.getStatus())
                .build();
    }

    public Customer toDomain(UUID id, CustomerRequest request) {
        Person person = Person.builder()
                .name(request.getName())
                .gender(Gender.valueOf(request.getGender().name()))
                .identification(request.getIdentification())
                .address(new Address(request.getAddress()))
                .phone(new Phone(request.getPhone()))
                .build();

        return Customer.builder()
                .customerId(new CustomerId(id))
                .person(person)
                .password(request.getPassword())
                .status(request.getStatus())
                .build();
    }

    public CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setCustomerId(customer.getCustomerId().getValue());
        response.setName(customer.getPerson().getName());
        response.setGender(customer.getPerson().getGender().name());
        response.setIdentification(customer.getPerson().getIdentification());
        response.setAddress(customer.getPerson().getAddress().getValue());
        response.setPhone(customer.getPerson().getPhone().getNumber());
        response.setStatus(customer.isStatus());
        return response;
    }
}
