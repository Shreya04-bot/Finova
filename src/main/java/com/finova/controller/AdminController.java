package com.finova.controller;

import com.finova.dto.AuditLogResponse;
import com.finova.dto.UserSummaryResponse;
import com.finova.security.CurrentUserUtil;
import com.finova.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    @PatchMapping("/accounts/{id}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable Long id) {
        String adminEmail = CurrentUserUtil.getCurrentUser().getUsername();
        adminService.blockAccount(adminEmail, id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/accounts/{id}/unblock")
    public ResponseEntity<Void> unblockAccount(@PathVariable Long id) {
        String adminEmail = CurrentUserUtil.getCurrentUser().getUsername();
        adminService.unblockAccount(adminEmail, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs() {
        return ResponseEntity.ok(adminService.getAuditLogs());
    }
}