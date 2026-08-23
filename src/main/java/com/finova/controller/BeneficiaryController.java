package com.finova.controller;

import com.finova.dto.BeneficiaryRequest;
import com.finova.dto.BeneficiaryResponse;
import com.finova.security.CurrentUserUtil;
import com.finova.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @GetMapping
    public ResponseEntity<List<BeneficiaryResponse>> getMyBeneficiaries() {
        Long userId = CurrentUserUtil.getCurrentUserId();
        return ResponseEntity.ok(beneficiaryService.getMyBeneficiaries(userId));
    }

    @PostMapping
    public ResponseEntity<BeneficiaryResponse> addBeneficiary(@Valid @RequestBody BeneficiaryRequest request) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        return ResponseEntity.ok(beneficiaryService.addBeneficiary(userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable Long id) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        beneficiaryService.deleteBeneficiary(userId, id);
        return ResponseEntity.ok().build();
    }
}