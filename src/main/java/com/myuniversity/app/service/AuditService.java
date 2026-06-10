package com.myuniversity.app.service;

import com.myuniversity.app.entity.AuditLog;
import com.myuniversity.app.repository.AuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String email, String details) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .email(email)
                .details(details)
                .timestamp(Instant.now())
                .build();
        auditLogRepository.save(auditLog);
        log.info("AUDIT - action: {}, email: {}, details: {}", action, email, details);
    }
}
