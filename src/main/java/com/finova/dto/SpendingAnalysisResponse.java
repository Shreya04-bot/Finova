package com.finova.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpendingAnalysisResponse {
    private BigDecimal totalSpent;
    private Map<String, BigDecimal> spendingByCategory;
    private Map<String, Double> percentageByCategory;
}