package com.banking.transaction.controller;

import com.banking.transaction.dto.TransferRequest;
import com.banking.transaction.entity.Transfer;
import com.banking.transaction.repository.TransferRepository;
import com.banking.transaction.service.TransferSagaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransferController {
    private final TransferSagaService sagaService;
    private final TransferRepository transferRepository;

    public TransferController(TransferSagaService sagaService,
                              TransferRepository transferRepository) {
        this.sagaService = sagaService;
        this.transferRepository = transferRepository;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        Transfer result = sagaService.transfer(request);
        HttpStatus status = "COMPLETED".equals(result.getStatus())
                ? HttpStatus.OK : HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(result);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getStatus(@PathVariable String transactionId) {
        return transferRepository.findById(transactionId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Transaction not found")));
    }
}
