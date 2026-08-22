package com.finova.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private boolean blocked;
}