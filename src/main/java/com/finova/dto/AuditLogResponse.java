package com.finova.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    private Long id;
    private String actor;
    private String action;
    private String targetEntity;
    private LocalDateTime timestamp;
}