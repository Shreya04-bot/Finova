package com.finova.controller;

import com.finova.dto.TransactionResponse;
import com.finova.entity.Transaction;
import com.finova.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository transactionRepository;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(@RequestParam Long accountId) {
        List<Transaction> transactions = transactionRepository
                .findBySenderAccountIdOrReceiverAccountIdOrderByCreatedAtDesc(accountId, accountId);

        List<TransactionResponse> response = transactions.stream()
                .map(t -> TransactionResponse.builder()
                        .transactionRef(t.getTransactionRef())
                        .type(t.getType().name())
                        .amount(t.getAmount())
                        .status(t.getStatus().name())
                        .description(t.getDescription())
                        .senderAccountNumber(t.getSenderAccount() != null ? t.getSenderAccount().getAccountNumber() : null)
                        .receiverAccountNumber(t.getReceiverAccount() != null ? t.getReceiverAccount().getAccountNumber() : null)
                        .createdAt(t.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}