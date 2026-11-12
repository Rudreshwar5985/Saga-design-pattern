package com.banking.account.controller;

import com.banking.account.dto.AccountOperationRequest;
import com.banking.account.entity.BankAccount;
import com.banking.account.repository.BankAccountRepository;
import com.banking.account.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;
    private final BankAccountRepository accountRepository;

    public AccountController(AccountService accountService,
                             BankAccountRepository accountRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/{id}")
    public BankAccount getAccount(@PathVariable Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    @PostMapping("/debit")
    public ResponseEntity<?> debit(@RequestBody AccountOperationRequest request) {
        accountService.debit(request);
        return ResponseEntity.ok(Map.of("message", "Debit successful"));
    }

    @PostMapping("/credit")
    public ResponseEntity<?> credit(@RequestBody AccountOperationRequest request) {
        accountService.credit(request);
        return ResponseEntity.ok(Map.of("message", "Credit successful"));
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestBody AccountOperationRequest request) {
        accountService.refund(request);
        return ResponseEntity.ok(Map.of("message", "Refund successful"));
    }
}
