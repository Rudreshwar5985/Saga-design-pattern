package com.banking.transaction.dto;

import java.math.BigDecimal;

public record TransferRequest(
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount
) {}
