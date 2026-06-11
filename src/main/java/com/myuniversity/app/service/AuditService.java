package com.myuniversity.app.service;

import com.myuniversity.app.dto.admin.AuditLogResponse;
import com.myuniversity.app.entity.AuditLog;
import com.myuniversity.app.repository.AuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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

    public Page<AuditLogResponse> getLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable)
                .map(AuditLogResponse::fromEntity);
    }
}
