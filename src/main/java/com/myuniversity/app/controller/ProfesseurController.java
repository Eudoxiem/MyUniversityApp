package com.myuniversity.app.controller;

import com.myuniversity.app.dto.ProfesseurDTO;
import com.myuniversity.app.service.ProfesseurService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/professeurs")
public class ProfesseurController {

    private final ProfesseurService service;

    public ProfesseurController(ProfesseurService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<ProfesseurDTO> getAll() {
        return service.findAll().stream()
                .map(ProfesseurDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public ResponseEntity<ProfesseurDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(ProfesseurDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfesseurDTO> create(@Valid @RequestBody ProfesseurDTO dto) {
        ProfesseurDTO saved = ProfesseurDTO.fromEntity(service.save(dto.toEntity()));
        log.info("Professeur créé - id: {}, email: {}", saved.getId(), saved.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfesseurDTO> update(@PathVariable Long id, @Valid @RequestBody ProfesseurDTO dto) {
        try {
            ProfesseurDTO updated = ProfesseurDTO.fromEntity(service.update(id, dto.toEntity()));
            log.info("Professeur modifié - id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Professeur non trouvé pour modification - id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> {
                    service.delete(id);
                    log.info("Professeur supprimé - id: {}", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
