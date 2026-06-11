package com.myuniversity.app.service;

import com.myuniversity.app.dto.admin.AuditLogResponse;
import com.myuniversity.app.entity.AuditLog;
import com.myuniversity.app.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService service;

    @Test
    void log_shouldSaveAuditLog() {
        service.log("LOGIN", "user@test.com", "Utilisateur connecté");

        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void log_shouldSaveWithCorrectFields() {
        service.log("LOGOUT", "admin@test.com", "Déconnexion");

        verify(auditLogRepository).save(argThat(auditLog ->
                "LOGOUT".equals(auditLog.getAction()) &&
                "admin@test.com".equals(auditLog.getEmail()) &&
                "Déconnexion".equals(auditLog.getDetails()) &&
                auditLog.getTimestamp() != null
        ));
    }

    @Test
    void getLogs_shouldReturnPagedResults() {
        AuditLog log1 = AuditLog.builder().id(1L).action("LOGIN").email("a@b.com").timestamp(Instant.now()).build();
        AuditLog log2 = AuditLog.builder().id(2L).action("LOGOUT").email("a@b.com").timestamp(Instant.now()).build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = new PageImpl<>(List.of(log1, log2));

        when(auditLogRepository.findAllByOrderByTimestampDesc(pageable)).thenReturn(page);

        Page<AuditLogResponse> result = service.getLogs(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("LOGIN", result.getContent().get(0).getAction());
        verify(auditLogRepository).findAllByOrderByTimestampDesc(pageable);
    }

    @Test
    void getLogs_whenEmpty_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(auditLogRepository.findAllByOrderByTimestampDesc(pageable)).thenReturn(Page.empty());

        Page<AuditLogResponse> result = service.getLogs(pageable);

        assertTrue(result.isEmpty());
    }
}
