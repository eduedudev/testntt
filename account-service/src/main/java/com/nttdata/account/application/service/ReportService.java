package com.nttdata.account.application.service;

import com.nttdata.account.application.usecase.AccountStatementReport;
import com.nttdata.account.application.usecase.ReportUseCase;
import com.nttdata.account.domain.exception.CustomerNotFoundException;
import com.nttdata.account.domain.model.CustomerId;
import com.nttdata.account.domain.port.out.AccountRepository;
import com.nttdata.account.domain.port.out.CustomerInfoRepository;
import com.nttdata.account.domain.port.out.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Report Service - Application layer
 * Generates account statements and reports
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService implements ReportUseCase {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final CustomerInfoRepository customerInfoRepository;

    @Override
    public Mono<AccountStatementReport> generateAccountStatement(
            CustomerId customerId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating account statement for customer: {} from {} to {}", 
                customerId, startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        return customerInfoRepository.findCustomerNameById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with ID: " + customerId)))
                .flatMap(customerName -> 
                    accountRepository.findByCustomerId(customerId)
                            .flatMap(account -> 
                                movementRepository.findByAccountIdAndDateBetween(
                                        account.getAccountId(), startDateTime, endDateTime)
                                .map(movement -> AccountStatementReport.MovementDetail.builder()
                                        .date(movement.getDate())
                                        .movementType(movement.getMovementType().name())
                                        .value(movement.getValue())
                                        .balance(movement.getBalance())
                                        .build())
                                .collectList()
                                .map(movements -> AccountStatementReport.AccountStatementDetail.builder()
                                        .accountNumber(account.getAccountNumber())
                                        .accountType(account.getAccountType().name())
                                        .currentBalance(account.getCurrentBalance())
                                        .movements(movements)
                                        .build())
                            )
                            .collectList()
                            .map(accountDetails -> AccountStatementReport.builder()
                                    .clientId(customerId)
                                    .clientName(customerName)
                                    .startDate(startDate)
                                    .endDate(endDate)
                                    .accounts(accountDetails)
                                    .build())
                )
                .doOnSuccess(report -> 
                    log.info("Account statement generated successfully for customer: {}", customerId));
    }
}
