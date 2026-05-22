package com.myuniversity.app.controller;

import com.myuniversity.app.dto.NoteDTO;
import com.myuniversity.app.entity.Note;
import com.myuniversity.app.repository.InscriptionRepository;
import com.myuniversity.app.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<NoteDTO> getAll() {
        return service.findAll().stream()
                .map(NoteDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(NoteDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NoteDTO> create(@Valid @RequestBody NoteDTO dto) {
        Note note = mapToEntity(dto);
        NoteDTO saved = NoteDTO.fromEntity(service.save(note));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> update(@PathVariable Long id, @Valid @RequestBody NoteDTO dto) {
        try {
            Note note = mapToEntity(dto);
            NoteDTO updated = NoteDTO.fromEntity(service.update(id, note));
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> {
                    service.delete(id);
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
