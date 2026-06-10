package com.myuniversity.app.controller;

import com.myuniversity.app.dto.EmploiDuTempsDTO;
import com.myuniversity.app.service.EmploiDuTempsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/emploi-du-temps")
public class EmploiDuTempsController {

    private final EmploiDuTempsService edtService;

    public EmploiDuTempsController(EmploiDuTempsService edtService) {
        this.edtService = edtService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<EmploiDuTempsDTO> getAll() {
        return edtService.findAll().stream()
                .map(EmploiDuTempsDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public ResponseEntity<EmploiDuTempsDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(EmploiDuTempsDTO.fromEntity(edtService.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/semestre")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<EmploiDuTempsDTO> getBySemestre(
            @RequestParam String semestre, @RequestParam("annee") String anneeAcademique) {
        return edtService.findBySemestre(semestre, anneeAcademique).stream()
                .map(EmploiDuTempsDTO::fromEntity)
                .toList();
    }

    @GetMapping("/professeur/{professeurId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public List<EmploiDuTempsDTO> getByProfesseur(
            @PathVariable Long professeurId,
            @RequestParam String semestre,
            @RequestParam("annee") String anneeAcademique) {
        return edtService.findByProfesseur(professeurId, semestre, anneeAcademique).stream()
                .map(EmploiDuTempsDTO::fromEntity)
                .toList();
    }

    @GetMapping("/salle/{salleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<EmploiDuTempsDTO> getBySalle(
            @PathVariable Long salleId,
            @RequestParam String semestre,
            @RequestParam("annee") String anneeAcademique) {
        return edtService.findBySalle(salleId, semestre, anneeAcademique).stream()
                .map(EmploiDuTempsDTO::fromEntity)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmploiDuTempsDTO> create(@Valid @RequestBody EmploiDuTempsDTO dto) {
        try {
            EmploiDuTempsDTO saved = EmploiDuTempsDTO.fromEntity(
                    edtService.save(dto.toEntity(), dto.getCoursId(), dto.getSalleId(), dto.getProfesseurId()));
            log.info("Emploi du temps créé - id: {}, coursId: {}", saved.getId(), dto.getCoursId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            log.warn("Échec création emploi du temps");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmploiDuTempsDTO> update(@PathVariable Long id, @Valid @RequestBody EmploiDuTempsDTO dto) {
        try {
            EmploiDuTempsDTO updated = EmploiDuTempsDTO.fromEntity(edtService.update(id, dto.toEntity()));
            log.info("Emploi du temps modifié - id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Emploi du temps non trouvé pour modification - id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            edtService.findById(id);
            edtService.delete(id);
            log.info("Emploi du temps supprimé - id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Emploi du temps non trouvé pour suppression - id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
