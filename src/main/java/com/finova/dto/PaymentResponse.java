package com.finova.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private String category;
    private String payee;
    private BigDecimal amount;
    private String status;
    private String accountNumber;
    private String transactionRef;
    private LocalDateTime createdAt;
}