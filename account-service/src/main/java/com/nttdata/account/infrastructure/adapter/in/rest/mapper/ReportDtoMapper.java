package com.nttdata.account.infrastructure.adapter.in.rest.mapper;

import com.nttdata.account.application.usecase.AccountStatementReport;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.AccountStatementDetail;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.AccountStatementResponse;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.MovementDetail;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Component
public class ReportDtoMapper {

    public AccountStatementResponse toResponse(AccountStatementReport report) {
        AccountStatementResponse response = new AccountStatementResponse();
        response.setClientId(report.getClientId().getValue());
        response.setClientName(report.getClientName());
        response.setStartDate(report.getStartDate());
        response.setEndDate(report.getEndDate());
        
        response.setAccounts(report.getAccounts().stream()
                .map(this::toAccountDetail)
                .collect(Collectors.toList()));
        
        return response;
    }

    private AccountStatementDetail toAccountDetail(AccountStatementReport.AccountStatementDetail accountDetail) {
        AccountStatementDetail detail = new AccountStatementDetail();
        detail.setAccountNumber(accountDetail.getAccountNumber());
        detail.setAccountType(accountDetail.getAccountType());
        detail.setCurrentBalance(accountDetail.getCurrentBalance().doubleValue());
        
        detail.setMovements(accountDetail.getMovements().stream()
                .map(this::toMovementDetail)
                .collect(Collectors.toList()));
        
        return detail;
    }

    private MovementDetail toMovementDetail(AccountStatementReport.MovementDetail movementDetail) {
        MovementDetail detail = new MovementDetail();
        detail.setDate(OffsetDateTime.of(movementDetail.getDate(), ZoneOffset.UTC));
        detail.setMovementType(movementDetail.getMovementType());
        detail.setValue(movementDetail.getValue().doubleValue());
        detail.setBalance(movementDetail.getBalance().doubleValue());
        return detail;
    }
}
