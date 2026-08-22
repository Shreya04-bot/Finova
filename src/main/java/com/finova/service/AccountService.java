package com.finova.service;

import com.finova.dto.AccountResponse;
import com.finova.entity.Account;
import com.finova.entity.AccountStatus;
import com.finova.entity.AccountType;
import com.finova.entity.User;
import com.finova.exception.ResourceNotFoundException;
import com.finova.repository.AccountRepository;
import com.finova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public List<AccountResponse> getMyAccounts(Long userId) {
        return accountRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse createAccount(Long userId, AccountType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .accountType(type)
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();

        accountRepository.save(account);
        return toResponse(account);
    }

    private String generateAccountNumber() {
        // simple simulated account number generator
        return "FIN" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 10);
    }

    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .build();
    }
}