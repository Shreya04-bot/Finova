package com.finova.controller;

import com.finova.dto.AccountResponse;
import com.finova.entity.AccountType;
import com.finova.security.CurrentUserUtil;
import com.finova.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts() {
        Long userId = CurrentUserUtil.getCurrentUserId();
        return ResponseEntity.ok(accountService.getMyAccounts(userId));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestParam AccountType type) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        return ResponseEntity.ok(accountService.createAccount(userId, type));
    }
}