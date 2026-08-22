package com.finova.dto;

import com.finova.entity.PaymentCategory;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Category is required")
    private PaymentCategory category;

    @NotBlank(message = "Payee is required")
    private String payee;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}