package com.banking.transaction.service;

import com.banking.transaction.dto.AccountOperationRequest;
import com.banking.transaction.dto.TransferRequest;
import com.banking.transaction.entity.Transfer;
import com.banking.transaction.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransferSagaService {
    private final RestClient accountClient;
    private final TransferRepository transferRepository;

    public TransferSagaService(RestClient accountClient,
                               TransferRepository transferRepository) {
        this.accountClient = accountClient;
        this.transferRepository = transferRepository;
    }

    public Transfer transfer(TransferRequest request) {
        validate(request);

        String transactionId = UUID.randomUUID().toString();
        Transfer transfer = new Transfer(transactionId, request.fromAccountId(),
                request.toAccountId(), request.amount());
        transferRepository.save(transfer);

        // Saga step 1: debit sender.
        try {
            accountClient.post()
                    .uri("/accounts/debit")
                    .body(new AccountOperationRequest(request.fromAccountId(),
                            request.amount(), transactionId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception debitFailure) {
            transfer.setStatus("FAILED");
            transfer.setFailureReason("Debit failed: " + safeMessage(debitFailure));
            return transferRepository.save(transfer);
        }

        // Saga step 2: credit receiver.
        try {
            accountClient.post()
                    .uri("/accounts/credit")
                    .body(new AccountOperationRequest(request.toAccountId(),
                            request.amount(), transactionId))
                    .retrieve()
                    .toBodilessEntity();

            transfer.setStatus("COMPLETED");
            return transferRepository.save(transfer);

        } catch (Exception creditFailure) {
            transfer.setFailureReason("Credit failed: " + safeMessage(creditFailure));

            // Compensation: refund sender because debit already succeeded.
            try {
                accountClient.post()
                        .uri("/accounts/refund")
                        .body(new AccountOperationRequest(request.fromAccountId(),
                                request.amount(), transactionId))
                        .retrieve()
                        .toBodilessEntity();

                transfer.setStatus("FAILED");
            } catch (Exception refundFailure) {
                transfer.setStatus("COMPENSATION_FAILED");
                transfer.setFailureReason(
                        "Credit failed: " + safeMessage(creditFailure)
                        + "; refund failed and needs recovery: "
                        + safeMessage(refundFailure));
            }
            return transferRepository.save(transfer);
        }
    }

    private void validate(TransferRequest request) {
        if (request == null || request.fromAccountId() == null ||
                request.toAccountId() == null ||
                request.fromAccountId().equals(request.toAccountId()) ||
                request.amount() == null ||
                request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Provide different valid account IDs and a positive amount");
        }
    }

    private String safeMessage(Exception ex) {
        String message = ex.getMessage();
        return message == null ? ex.getClass().getSimpleName()
                : message.substring(0, Math.min(message.length(), 400));
    }
}
