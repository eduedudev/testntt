package com.nttdata.account.infrastructure.adapter.in.rest;

import com.nttdata.account.application.usecase.ReportUseCase;
import com.nttdata.account.domain.model.CustomerId;
import com.nttdata.account.infrastructure.adapter.in.rest.api.ReportsApi;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.AccountStatementResponse;
import com.nttdata.account.infrastructure.adapter.in.rest.mapper.ReportDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportController implements ReportsApi {

    private final ReportUseCase reportUseCase;
    private final ReportDtoMapper reportDtoMapper;

    @Override
    public Mono<ResponseEntity<AccountStatementResponse>> getAccountStatement(
            UUID clientId,
            LocalDate startDate,
            LocalDate endDate,
            ServerWebExchange exchange) {
        
        log.info("Generating account statement for client: {} from {} to {}", 
                clientId, startDate, endDate);
        
        return reportUseCase.generateAccountStatement(CustomerId.of(clientId), startDate, endDate)
                .map(reportDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Account statement generated for client: {}", clientId))
                .doOnError(error -> log.error("Error generating account statement for client: {}", 
                        clientId, error));
    }
}
