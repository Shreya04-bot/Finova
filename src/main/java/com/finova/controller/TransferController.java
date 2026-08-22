package com.finova.controller;

import com.finova.dto.TransactionResponse;
import com.finova.dto.TransferRequest;
import com.finova.security.CurrentUserUtil;
import com.finova.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        return ResponseEntity.ok(transferService.transfer(userId, request));
    }
}