package com.webauditor.backend.CompanyService;

import com.webauditor.backend.entity.AdminAuditLog;
import com.webauditor.backend.repository.AdminAuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AdminAuditService {

    private final AdminAuditLogRepository repository;

    public AdminAuditService(AdminAuditLogRepository repository) {
        this.repository = repository;
    }

    public void record(
        Authentication authentication,
        HttpServletRequest request,
        String action,
        String targetType,
        Object targetId,
        String details
    ) {
        AdminAuditLog log = new AdminAuditLog();
        log.setActor(authentication == null ? "UNKNOWN" : authentication.getName());
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId == null ? null : targetId.toString());
        log.setDetails(limit(details, 500));
        log.setIpAddress(clientIp(request));
        repository.save(log);
    }

    public static String clientIp(HttpServletRequest request) {
        return limit(request.getRemoteAddr(), 45);
    }

    private static String limit(String value, int maximumLength) {
        if (value == null || value.length() <= maximumLength) {
            return value;
        }
        return value.substring(0, maximumLength);
    }
}
