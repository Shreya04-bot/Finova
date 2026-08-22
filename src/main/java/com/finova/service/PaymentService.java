package com.finova.service;

import com.finova.dto.PaymentRequest;
import com.finova.dto.PaymentResponse;
import com.finova.entity.*;
import com.finova.exception.AccountBlockedException;
import com.finova.exception.InsufficientBalanceException;
import com.finova.exception.ResourceNotFoundException;
import com.finova.repository.AccountRepository;
import com.finova.repository.PaymentRepository;
import com.finova.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    @Transactional
    public PaymentResponse makePayment(Long requestingUserId, PaymentRequest request) {

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(requestingUserId)) {
            throw new AccountBlockedException("You can only pay from your own account");
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountBlockedException("Account is not active");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // Debit the account
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        // Create the transaction record (PAYMENT type, no receiver account — money leaves the system)
        Transaction transaction = Transaction.builder()
                .transactionRef(generateTransactionRef())
                .type(TransactionType.PAYMENT)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .description(request.getCategory() + " payment to " + request.getPayee())
                .senderAccount(account)
                .receiverAccount(null)
                .build();
        transactionRepository.save(transaction);

        // Create the payment record, linked to the transaction
        Payment payment = Payment.builder()
                .category(request.getCategory())
                .amount(request.getAmount())
                .payee(request.getPayee())
                .status(PaymentStatus.SUCCESS)
                .transaction(transaction)
                .account(account)
                .build();
        paymentRepository.save(payment);

        notificationService.createNotification(
                account.getUser(),
                "Payment Successful",
                "₹" + request.getAmount() + " payment to " + request.getPayee() + " was successful."
        );

        return toResponse(payment);
    }

    public List<PaymentResponse> getPaymentsForAccount(Long accountId) {
        return paymentRepository.findByAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private String generateTransactionRef() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 5);
        return "TXN-" + datePart + "-" + randomPart;
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .category(p.getCategory().name())
                .payee(p.getPayee())
                .amount(p.getAmount())
                .status(p.getStatus().name())
                .accountNumber(p.getAccount().getAccountNumber())
                .transactionRef(p.getTransaction().getTransactionRef())
                .createdAt(p.getCreatedAt())
                .build();
    }
}