package com.banking.transaction.dto;

import java.math.BigDecimal;

public record AccountOperationRequest(
        Long accountId,
        BigDecimal amount,
        String transactionId
) {}
