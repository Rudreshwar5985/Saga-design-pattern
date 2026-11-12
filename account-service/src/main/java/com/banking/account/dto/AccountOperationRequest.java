package com.banking.account.dto;

import java.math.BigDecimal;

public record AccountOperationRequest(
        Long accountId,
        BigDecimal amount,
        String transactionId
) {}
