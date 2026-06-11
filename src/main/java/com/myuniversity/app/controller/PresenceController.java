package com.myuniversity.app.controller;

import com.myuniversity.app.dto.presence.AppelRequest;
import com.myuniversity.app.dto.presence.PresenceDTO;
import com.myuniversity.app.dto.presence.PresenceRequest;
import com.myuniversity.app.service.PresenceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/presences")
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public PresenceDTO getById(@PathVariable Long id) {
        return presenceService.getPresenceById(id);
    }

    @GetMapping("/cours/{coursId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public List<PresenceDTO> getByCoursAndDate(
            @PathVariable Long coursId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return presenceService.getPresencesByCoursAndDate(coursId, date);
    }

    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<PresenceDTO> getByEtudiant(@PathVariable Long etudiantId) {
        return presenceService.getPresencesByEtudiant(etudiantId);
    }

    @GetMapping("/etudiant/{etudiantId}/cours/{coursId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<PresenceDTO> getByEtudiantAndCours(
            @PathVariable Long etudiantId, @PathVariable Long coursId) {
        return presenceService.getPresencesByEtudiantAndCours(etudiantId, coursId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public ResponseEntity<PresenceDTO> createPresence(@Valid @RequestBody PresenceRequest request) {
        try {
            PresenceDTO created = presenceService.createPresence(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/appel")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public ResponseEntity<List<PresenceDTO>> faireAppel(@Valid @RequestBody AppelRequest request) {
        try {
            return ResponseEntity.ok(presenceService.faireAppel(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public ResponseEntity<PresenceDTO> updatePresence(
            @PathVariable Long id, @Valid @RequestBody PresenceRequest request) {
        try {
            return ResponseEntity.ok(presenceService.updatePresence(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePresence(@PathVariable Long id) {
        presenceService.deletePresence(id);
        return ResponseEntity.noContent().build();
    }
}
