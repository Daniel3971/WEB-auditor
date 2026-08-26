package com.webauditor.backend.controller;

import com.webauditor.backend.entity.AdminAuditLog;
import com.webauditor.backend.repository.AdminAuditLogRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AdminAuditLogController {

    private final AdminAuditLogRepository repository;

    public AdminAuditLogController(AdminAuditLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<AdminAuditLog> recentAuditLogs() {
        return repository.findTop100ByOrderByCreatedAtDesc();
    }
}
