package com.myuniversity.app.controller;

import com.myuniversity.app.dto.NoteDTO;
import com.myuniversity.app.entity.Note;
import com.myuniversity.app.repository.InscriptionRepository;
import com.myuniversity.app.service.NoteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService service;
    private final InscriptionRepository inscriptionRepository;

    public NoteController(NoteService service, InscriptionRepository inscriptionRepository) {
        this.service = service;
        this.inscriptionRepository = inscriptionRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<NoteDTO> getAll() {
        return service.findAll().stream()
                .map(NoteDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public ResponseEntity<NoteDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(NoteDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public ResponseEntity<NoteDTO> create(@Valid @RequestBody NoteDTO dto) {
        Note note = mapToEntity(dto);
        NoteDTO saved = NoteDTO.fromEntity(service.save(note));
        log.info("Note créée - id: {}, inscriptionId: {}, valeur: {}", saved.getId(), dto.getInscriptionId(), dto.getValeur());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public ResponseEntity<NoteDTO> update(@PathVariable Long id, @Valid @RequestBody NoteDTO dto) {
        try {
            Note note = mapToEntity(dto);
            NoteDTO updated = NoteDTO.fromEntity(service.update(id, note));
            log.info("Note modifiée - id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Note non trouvée pour modification - id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> {
                    service.delete(id);
                    log.info("Note supprimée - id: {}", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Note mapToEntity(NoteDTO dto) {
        Note note = new Note();
        note.setId(dto.getId());
        note.setValeur(dto.getValeur());
        note.setCoefficient(dto.getCoefficient());
        note.setType(dto.getType());
        note.setDateSaisie(dto.getDateSaisie());
        inscriptionRepository.findById(dto.getInscriptionId()).ifPresent(note::setInscription);
        return note;
    }
}
