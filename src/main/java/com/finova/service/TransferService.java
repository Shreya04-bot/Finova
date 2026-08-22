package com.finova.service;

import com.finova.dto.TransactionResponse;
import com.finova.dto.TransferRequest;
import com.finova.entity.*;
import com.finova.event.MoneyTransferredEvent;
import com.finova.exception.AccountBlockedException;
import com.finova.exception.InsufficientBalanceException;
import com.finova.exception.ResourceNotFoundException;
import com.finova.repository.AccountRepository;
import com.finova.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TransactionResponse transfer(Long requestingUserId, TransferRequest request) {

        Account sender = accountRepository
                .findByAccountNumber(request.getSenderAccountNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Sender account not found"));

        Account receiver = accountRepository
                .findByAccountNumber(request.getReceiverAccountNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Receiver account not found"));

        // Security check: sender account must belong to requesting user
        if (!sender.getUser().getId().equals(requestingUserId)) {
            throw new AccountBlockedException(
                    "You can only transfer from your own account"
            );
        }

        // Check sender account status
        if (sender.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountBlockedException(
                    "Sender account is not active"
            );
        }

        // Check receiver account status
        if (receiver.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountBlockedException(
                    "Receiver account is not active"
            );
        }

        // Validate amount
        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero"
            );
        }

        // Check sufficient balance
        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance"
            );
        }

        // Debit sender
        sender.setBalance(
                sender.getBalance().subtract(request.getAmount())
        );

        // Credit receiver
        receiver.setBalance(
                receiver.getBalance().add(request.getAmount())
        );

        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .transactionRef(generateTransactionRef())
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .senderAccount(sender)
                .receiverAccount(receiver)
                .build();

        transactionRepository.save(transaction);

        // Publish event instead of directly creating notifications
        eventPublisher.publishEvent(
                new MoneyTransferredEvent(
                        this,
                        sender,
                        receiver,
                        request.getAmount()
                )
        );

        return toResponse(transaction);
    }

    private String generateTransactionRef() {

        String datePart = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String randomPart = UUID.randomUUID()
                .toString()
                .replaceAll("[^0-9]", "");

        if (randomPart.length() < 5) {
            randomPart = String.format(
                    "%05d",
                    Math.abs(UUID.randomUUID().hashCode()) % 100000
            );
        }

        randomPart = randomPart.substring(0, 5);

        return "TXN-" + datePart + "-" + randomPart;
    }

    private TransactionResponse toResponse(Transaction t) {

        return TransactionResponse.builder()
                .transactionRef(t.getTransactionRef())
                .type(t.getType().name())
                .amount(t.getAmount())
                .status(t.getStatus().name())
                .description(t.getDescription())
                .senderAccountNumber(
                        t.getSenderAccount() != null
                                ? t.getSenderAccount().getAccountNumber()
                                : null
                )
                .receiverAccountNumber(
                        t.getReceiverAccount() != null
                                ? t.getReceiverAccount().getAccountNumber()
                                : null
                )
                .createdAt(t.getCreatedAt())
                .build();
    }
}