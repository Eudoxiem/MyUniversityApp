package com.myuniversity.app.controller;

import com.myuniversity.app.dto.CoursDTO;
import com.myuniversity.app.entity.Cours;
import com.myuniversity.app.repository.ProfesseurRepository;
import com.myuniversity.app.repository.SalleRepository;
import com.myuniversity.app.service.CoursService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cours")
public class CoursController {

    private final CoursService service;
    private final ProfesseurRepository professeurRepository;
    private final SalleRepository salleRepository;

    public CoursController(CoursService service, ProfesseurRepository professeurRepository, SalleRepository salleRepository) {
        this.service = service;
        this.professeurRepository = professeurRepository;
        this.salleRepository = salleRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<CoursDTO> getAll() {
        return service.findAll().stream()
                .map(CoursDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public ResponseEntity<CoursDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(CoursDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoursDTO> create(@Valid @RequestBody CoursDTO dto) {
        Cours cours = mapToEntity(dto);
        CoursDTO saved = CoursDTO.fromEntity(service.save(cours));
        log.info("Cours créé - id: {}, code: {}, nom: {}", saved.getId(), saved.getCode(), saved.getNom());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoursDTO> update(@PathVariable Long id, @Valid @RequestBody CoursDTO dto) {
        try {
            Cours cours = mapToEntity(dto);
            CoursDTO updated = CoursDTO.fromEntity(service.update(id, cours));
            log.info("Cours modifié - id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Cours non trouvé pour modification - id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> {
                    service.delete(id);
                    log.info("Cours supprimé - id: {}", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Cours mapToEntity(CoursDTO dto) {
        Cours cours = new Cours();
        cours.setId(dto.getId());
        cours.setCode(dto.getCode());
        cours.setNom(dto.getNom());
        cours.setCredits(dto.getCredits());
        cours.setDescription(dto.getDescription());
        if (dto.getProfesseurId() != null) {
            cours.setProfesseur(professeurRepository.findById(dto.getProfesseurId())
                    .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'id : " + dto.getProfesseurId())));
        }
        if (dto.getSalleId() != null) {
            cours.setSalle(salleRepository.findById(dto.getSalleId())
                    .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'id : " + dto.getSalleId())));
        }
        return cours;
    }
}
