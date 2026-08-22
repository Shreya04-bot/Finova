package com.finova.service;

import com.finova.entity.AuditLog;
import com.finova.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(String actor, String action, String targetEntity) {
        AuditLog auditLog = AuditLog.builder()
                .actor(actor)
                .action(action)
                .targetEntity(targetEntity)
                .build();
        auditLogRepository.save(auditLog);
    }
}