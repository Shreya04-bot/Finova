package com.finova.service;

import com.finova.dto.AuditLogResponse;
import com.finova.dto.TransactionResponse;
import com.finova.dto.UserSummaryResponse;
import com.finova.entity.AccountStatus;
import com.finova.entity.Transaction;
import com.finova.entity.User;
import com.finova.exception.ResourceNotFoundException;
import com.finova.repository.AccountRepository;
import com.finova.repository.AuditLogRepository;
import com.finova.repository.TransactionRepository;
import com.finova.repository.UserRepository;
import com.finova.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService;

    public List<UserSummaryResponse> getAllCustomers() {
        return userRepository.findAll().stream()
                .map(this::toUserSummary)
                .collect(Collectors.toList());
    }

    @Transactional
    public void blockAccount(String adminEmail, Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        account.setStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);

        auditLogService.log(adminEmail, "BLOCK_ACCOUNT", account.getAccountNumber());
    }

    @Transactional
    public void unblockAccount(String adminEmail, Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);

        auditLogService.log(adminEmail, "UNBLOCK_ACCOUNT", account.getAccountNumber());
    }

    public List<AuditLogResponse> getAuditLogs() {
        return auditLogRepository.findAll().stream()
                .map(log -> AuditLogResponse.builder()
                        .id(log.getId())
                        .actor(log.getActor())
                        .action(log.getAction())
                        .targetEntity(log.getTargetEntity())
                        .timestamp(log.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }

    private UserSummaryResponse toUserSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .blocked(user.isBlocked())
                .build();
    }
}