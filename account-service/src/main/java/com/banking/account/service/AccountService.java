package com.banking.account.service;

import com.banking.account.dto.AccountOperationRequest;
import com.banking.account.entity.AccountOperation;
import com.banking.account.entity.BankAccount;
import com.banking.account.repository.AccountOperationRepository;
import com.banking.account.repository.BankAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    private final BankAccountRepository accountRepository;
    private final AccountOperationRepository operationRepository;

    public AccountService(BankAccountRepository accountRepository,
                          AccountOperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @Transactional
    public void debit(AccountOperationRequest request) {
        validate(request);
        String key = request.transactionId() + ":DEBIT";
        if (operationRepository.existsByOperationKey(key)) return;

        BankAccount account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        account.setBalance(account.getBalance().subtract(request.amount()));
        accountRepository.save(account);
        operationRepository.save(new AccountOperation(
                key, "DEBIT", request.accountId(), request.amount()));
    }

    @Transactional
    public void credit(AccountOperationRequest request) {
        validate(request);
        String key = request.transactionId() + ":CREDIT";
        if (operationRepository.existsByOperationKey(key)) return;

        BankAccount account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));

        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);
        operationRepository.save(new AccountOperation(
                key, "CREDIT", request.accountId(), request.amount()));
    }

    @Transactional
    public void refund(AccountOperationRequest request) {
        validate(request);
        String key = request.transactionId() + ":REFUND";
        if (operationRepository.existsByOperationKey(key)) return;

        BankAccount account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));

        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);
        operationRepository.save(new AccountOperation(
                key, "REFUND", request.accountId(), request.amount()));
    }

    private void validate(AccountOperationRequest request) {
        if (request == null || request.accountId() == null ||
            request.transactionId() == null || request.transactionId().isBlank() ||
            request.amount() == null ||
            request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valid accountId, transactionId and positive amount are required");
        }
    }
}
