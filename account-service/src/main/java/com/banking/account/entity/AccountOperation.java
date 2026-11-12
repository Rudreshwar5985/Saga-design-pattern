package com.banking.account.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_operations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"operation_key"}))
public class AccountOperation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_key", nullable = false, unique = true)
    private String operationKey;

    @Column(nullable = false)
    private String operationType;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected AccountOperation() {}

    public AccountOperation(String operationKey, String operationType,
                            Long accountId, BigDecimal amount) {
        this.operationKey = operationKey;
        this.operationType = operationType;
        this.accountId = accountId;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }
}
