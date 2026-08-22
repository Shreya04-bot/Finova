package com.finova.controller;

import com.finova.dto.PaymentRequest;
import com.finova.dto.PaymentResponse;
import com.finova.security.CurrentUserUtil;
import com.finova.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(@Valid @RequestBody PaymentRequest request) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        return ResponseEntity.ok(paymentService.makePayment(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments(@RequestParam Long accountId) {
        return ResponseEntity.ok(paymentService.getPaymentsForAccount(accountId));
    }
}