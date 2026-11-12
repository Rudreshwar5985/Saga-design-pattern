package com.banking.account.config;

import com.banking.account.entity.BankAccount;
import com.banking.account.repository.BankAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedAccounts(BankAccountRepository repository) {
        return args -> {
            if (!repository.existsById(101L)) {
                repository.save(new BankAccount(101L, new BigDecimal("20000.00")));
            }
            if (!repository.existsById(102L)) {
                repository.save(new BankAccount(102L, new BigDecimal("10000.00")));
            }
        };
    }
}
