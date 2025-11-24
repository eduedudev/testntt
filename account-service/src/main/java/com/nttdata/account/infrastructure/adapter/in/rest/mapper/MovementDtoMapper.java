package com.nttdata.account.infrastructure.adapter.in.rest.mapper;

import com.nttdata.account.domain.model.*;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.MovementRequest;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.MovementResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class MovementDtoMapper {

    public Movement toDomain(MovementRequest request) {
        return Movement.create(
                MovementType.valueOf(request.getMovementType().getValue().toUpperCase()),
                BigDecimal.valueOf(request.getValue()),
                BigDecimal.ZERO, // balance will be set by service
                AccountId.of(request.getAccountId()),
                "" // accountNumber will be set by service
        );
    }

    public MovementResponse toResponse(Movement movement) {
        MovementResponse response = new MovementResponse();
        response.setMovementId(movement.getMovementId().getValue());
        response.setDate(OffsetDateTime.of(movement.getDate(), ZoneOffset.UTC));
        response.setMovementType(movement.getMovementType().toString());
        response.setValue(movement.getValue().doubleValue());
        response.setBalance(movement.getBalance().doubleValue());
        response.setAccountId(movement.getAccountId().getValue());
        response.setAccountNumber(movement.getAccountNumber());
        return response;
    }
}
