package com.myuniversity.app.controller;

import com.myuniversity.app.dto.GradeDTO;
import com.myuniversity.app.entity.Grade;
import com.myuniversity.app.repository.InscriptionRepository;
import com.myuniversity.app.service.GradeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService service;
    private final InscriptionRepository inscriptionRepository;

    public GradeController(GradeService service, InscriptionRepository inscriptionRepository) {
        this.service = service;
        this.inscriptionRepository = inscriptionRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<GradeDTO> getAll() {
        return service.findAll().stream()
                .map(GradeDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public ResponseEntity<GradeDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(GradeDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public ResponseEntity<GradeDTO> create(@Valid @RequestBody GradeDTO dto) {
        Grade grade = mapToEntity(dto);
        GradeDTO saved = GradeDTO.fromEntity(service.save(grade));
        log.info("Grade créé - id: {}, inscriptionId: {}", saved.getId(), dto.getInscriptionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public ResponseEntity<GradeDTO> update(@PathVariable Long id, @Valid @RequestBody GradeDTO dto) {
        try {
            Grade grade = mapToEntity(dto);
            GradeDTO updated = GradeDTO.fromEntity(service.update(id, grade));
            log.info("Grade modifié - id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Grade non trouvé pour modification - id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> {
                    service.delete(id);
                    log.info("Grade supprimé - id: {}", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Grade mapToEntity(GradeDTO dto) {
        Grade grade = new Grade();
        grade.setId(dto.getId());
        grade.setValeurFinale(dto.getValeurFinale());
        grade.setMention(dto.getMention());
        grade.setDateValidation(dto.getDateValidation());
        grade.setInscription(inscriptionRepository.findById(dto.getInscriptionId())
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée avec l'id : " + dto.getInscriptionId())));
        return grade;
    }
}
