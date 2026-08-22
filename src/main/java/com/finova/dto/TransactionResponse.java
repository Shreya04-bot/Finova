package com.finova.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private String transactionRef;
    private String type;
    private BigDecimal amount;
    private String status;
    private String description;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private LocalDateTime createdAt;
}