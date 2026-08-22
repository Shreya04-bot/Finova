package com.finova.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;
}