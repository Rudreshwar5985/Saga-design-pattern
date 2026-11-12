package com.banking.account.repository;

import com.banking.account.entity.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    boolean existsByOperationKey(String operationKey);
}
