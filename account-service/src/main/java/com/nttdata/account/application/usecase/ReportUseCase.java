package com.nttdata.account.application.usecase;

import com.nttdata.account.domain.model.CustomerId;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Input port - Use cases for Report generation
 */
public interface ReportUseCase {
    
    /**
     * Generate account statement report for a customer
     */
    Mono<AccountStatementReport> generateAccountStatement(
            CustomerId customerId, LocalDate startDate, LocalDate endDate);
}
