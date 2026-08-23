package com.finova.controller;

import com.finova.dto.SpendingAnalysisResponse;
import com.finova.service.AiAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    @GetMapping("/spending-analysis")
    public ResponseEntity<SpendingAnalysisResponse> analyzeSpending(@RequestParam Long accountId) {
        return ResponseEntity.ok(aiAssistantService.analyzeSpending(accountId));
    }
}