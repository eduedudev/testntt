package com.nttdata.account.application.usecase;

import com.nttdata.account.domain.model.CustomerId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for Account Statement Report
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementReport {
    private CustomerId clientId;
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<AccountStatementDetail> accounts;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountStatementDetail {
        private String accountNumber;
        private String accountType;
        private java.math.BigDecimal currentBalance;
        private List<MovementDetail> movements;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementDetail {
        private java.time.LocalDateTime date;
        private String movementType;
        private java.math.BigDecimal value;
        private java.math.BigDecimal balance;
    }
}
