package com.nttdata.account.infrastructure.adapter.in.rest.mapper;

import com.nttdata.account.domain.model.Account;
import com.nttdata.account.domain.model.AccountId;
import com.nttdata.account.domain.model.AccountType;
import com.nttdata.account.domain.model.CustomerId;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.AccountRequest;
import com.nttdata.account.infrastructure.adapter.in.rest.dto.AccountResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountDtoMapper {

    public Account toDomain(AccountRequest request) {
        return Account.create(
                request.getAccountNumber(),
                AccountType.valueOf(request.getAccountType().getValue().toUpperCase()),
                BigDecimal.valueOf(request.getInitialBalance()),
                request.getStatus() != null ? request.getStatus() : true,
                CustomerId.of(request.getCustomerId()),
                "" // customerName will be set by service
        );
    }

    public AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getAccountId().getValue());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType().toString());
        response.setCurrentBalance(account.getCurrentBalance().doubleValue());
        response.setStatus(account.isStatus());
        response.setCustomerId(account.getCustomerId().getValue());
        response.setCustomerName(account.getCustomerName());
        return response;
    }
}
