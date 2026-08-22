package com.finova.service;

import com.finova.dto.TransactionResponse;
import com.finova.dto.TransferRequest;
import com.finova.entity.*;
import com.finova.exception.InsufficientBalanceException;
import com.finova.repository.AccountRepository;
import com.finova.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransferService transferService;

    private User user;
    private Account sender;
    private Account receiver;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("aarohi@example.com").build();

        User otherUser = User.builder().id(2L).email("rahul@example.com").build();

        sender = Account.builder()
                .id(1L)
                .accountNumber("FIN1111111111")
                .balance(new BigDecimal("1000.00"))
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();

        receiver = Account.builder()
                .id(2L)
                .accountNumber("FIN2222222222")
                .balance(new BigDecimal("500.00"))
                .status(AccountStatus.ACTIVE)
                .user(otherUser)
                .build();
    }

    @Test
    void transfer_shouldDebitSenderAndCreditReceiver_whenBalanceIsSufficient() {
        TransferRequest request = TransferRequest.builder()
                .senderAccountNumber("FIN1111111111")
                .receiverAccountNumber("FIN2222222222")
                .amount(new BigDecimal("200.00"))
                .description("Test transfer")
                .build();

        when(accountRepository.findByAccountNumber("FIN1111111111")).thenReturn(Optional.of(sender));
        when(accountRepository.findByAccountNumber("FIN2222222222")).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transferService.transfer(1L, request);

        assertThat(sender.getBalance()).isEqualByComparingTo("800.00");
        assertThat(receiver.getBalance()).isEqualByComparingTo("700.00");
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getAmount()).isEqualByComparingTo("200.00");

        verify(accountRepository).save(sender);
        verify(accountRepository).save(receiver);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void transfer_shouldThrowInsufficientBalanceException_whenAmountExceedsBalance() {
        TransferRequest request = TransferRequest.builder()
                .senderAccountNumber("FIN1111111111")
                .receiverAccountNumber("FIN2222222222")
                .amount(new BigDecimal("5000.00")) // more than sender's 1000 balance
                .build();

        when(accountRepository.findByAccountNumber("FIN1111111111")).thenReturn(Optional.of(sender));
        when(accountRepository.findByAccountNumber("FIN2222222222")).thenReturn(Optional.of(receiver));

        assertThatThrownBy(() -> transferService.transfer(1L, request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient balance");

        // Balances must remain unchanged since the transfer was rejected
        assertThat(sender.getBalance()).isEqualByComparingTo("1000.00");
        assertThat(receiver.getBalance()).isEqualByComparingTo("500.00");

        verify(transactionRepository, never()).save(any());
    }
}