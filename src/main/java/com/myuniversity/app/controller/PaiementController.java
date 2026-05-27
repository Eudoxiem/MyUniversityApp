package com.myuniversity.app.controller;

import com.myuniversity.app.dto.PaiementDTO;
import com.myuniversity.app.service.PaiementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public List<PaiementDTO> getAll() {
        return paiementService.findAll().stream()
                .map(PaiementDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public ResponseEntity<PaiementDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(PaiementDTO.fromEntity(paiementService.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<PaiementDTO> getByEtudiant(@PathVariable Long etudiantId) {
        return paiementService.findByEtudiantId(etudiantId).stream()
                .map(PaiementDTO::fromEntity)
                .toList();
    }

    @GetMapping("/en-retard")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaiementDTO> getPaiementsEnRetard() {
        return paiementService.findPaiementsEnRetard().stream()
                .map(PaiementDTO::fromEntity)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaiementDTO> create(@Valid @RequestBody PaiementDTO dto) {
        try {
            PaiementDTO saved = PaiementDTO.fromEntity(
                    paiementService.save(dto.toEntity(), dto.getEtudiantId()));
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaiementDTO> update(@PathVariable Long id, @Valid @RequestBody PaiementDTO dto) {
        try {
            PaiementDTO updated = PaiementDTO.fromEntity(paiementService.update(id, dto.toEntity()));
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            paiementService.findById(id);
            paiementService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
