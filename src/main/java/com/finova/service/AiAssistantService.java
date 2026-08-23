package com.finova.service;

import com.finova.dto.SpendingAnalysisResponse;
import com.finova.entity.Payment;
import com.finova.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAssistantService {

    private final PaymentRepository paymentRepository;

    public SpendingAnalysisResponse analyzeSpending(Long accountId) {
        List<Payment> payments = paymentRepository.findByAccountIdOrderByCreatedAtDesc(accountId);

        Map<String, BigDecimal> byCategory = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory().name(),
                        Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)
                ));

        BigDecimal total = byCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Double> percentages = new HashMap<>();
        byCategory.forEach((category, amount) -> {
            double pct = total.compareTo(BigDecimal.ZERO) == 0
                    ? 0.0
                    : amount.divide(total, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            percentages.put(category, pct);
        });

        return SpendingAnalysisResponse.builder()
                .totalSpent(total)
                .spendingByCategory(byCategory)
                .percentageByCategory(percentages)
                .build();
    }
}