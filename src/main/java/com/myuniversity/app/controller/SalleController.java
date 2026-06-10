package com.myuniversity.app.controller;

import com.myuniversity.app.dto.SalleDTO;
import com.myuniversity.app.service.SalleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/salles")
public class SalleController {

    private final SalleService service;

    public SalleController(SalleService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<SalleDTO> getAll() {
        return service.findAll().stream()
                .map(SalleDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public ResponseEntity<SalleDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(SalleDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalleDTO> create(@Valid @RequestBody SalleDTO dto) {
        SalleDTO saved = SalleDTO.fromEntity(service.save(dto.toEntity()));
        log.info("Salle créée - id: {}, code: {}, nom: {}", saved.getId(), saved.getCode(), saved.getNom());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalleDTO> update(@PathVariable Long id, @Valid @RequestBody SalleDTO dto) {
        try {
            SalleDTO updated = SalleDTO.fromEntity(service.update(id, dto.toEntity()));
            log.info("Salle modifiée - id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Salle non trouvée pour modification - id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> {
                    service.delete(id);
                    log.info("Salle supprimée - id: {}", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
