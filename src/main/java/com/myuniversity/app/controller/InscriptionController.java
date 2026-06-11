package com.myuniversity.app.controller;

import com.myuniversity.app.dto.InscriptionDTO;
import com.myuniversity.app.entity.Inscription;
import com.myuniversity.app.repository.CoursRepository;
import com.myuniversity.app.repository.EtudiantRepository;
import com.myuniversity.app.service.InscriptionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionController {

    private final InscriptionService service;
    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;

    public InscriptionController(InscriptionService service, EtudiantRepository etudiantRepository, CoursRepository coursRepository) {
        this.service = service;
        this.etudiantRepository = etudiantRepository;
        this.coursRepository = coursRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<InscriptionDTO> getAll() {
        return service.findAll().stream()
                .map(InscriptionDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR') or @securityHelper.estProprietaireInscription(#id)")
    public ResponseEntity<InscriptionDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(InscriptionDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InscriptionDTO> create(@Valid @RequestBody InscriptionDTO dto) {
        Inscription inscription = mapToEntity(dto);
        InscriptionDTO saved = InscriptionDTO.fromEntity(service.save(inscription));
        log.info("Inscription créée - id: {}, étudiantId: {}, coursId: {}", saved.getId(), dto.getEtudiantId(), dto.getCoursId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InscriptionDTO> update(@PathVariable Long id, @Valid @RequestBody InscriptionDTO dto) {
        try {
            Inscription inscription = mapToEntity(dto);
            InscriptionDTO updated = InscriptionDTO.fromEntity(service.update(id, inscription));
            log.info("Inscription modifiée - id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Inscription non trouvée pour modification - id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> {
                    service.delete(id);
                    log.info("Inscription supprimée - id: {}", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Inscription mapToEntity(InscriptionDTO dto) {
        Inscription inscription = new Inscription();
        inscription.setId(dto.getId());
        inscription.setDateInscription(dto.getDateInscription());
        inscription.setEtudiant(etudiantRepository.findById(dto.getEtudiantId())
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'id : " + dto.getEtudiantId())));
        inscription.setCours(coursRepository.findById(dto.getCoursId())
                .orElseThrow(() -> new RuntimeException("Cours non trouvé avec l'id : " + dto.getCoursId())));
        return inscription;
    }
}
