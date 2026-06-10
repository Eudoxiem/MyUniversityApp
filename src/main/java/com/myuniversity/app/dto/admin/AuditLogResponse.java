package com.myuniversity.app.dto.admin;

import com.myuniversity.app.entity.AuditLog;
import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuditLogResponse {
    private Long id;
    private String action;
    private String email;
    private String details;
    private Instant timestamp;

    public static AuditLogResponse fromEntity(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .action(auditLog.getAction())
                .email(auditLog.getEmail())
                .details(auditLog.getDetails())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
