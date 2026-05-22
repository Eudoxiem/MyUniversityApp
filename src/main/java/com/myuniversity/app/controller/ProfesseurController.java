package com.myuniversity.app.controller;

import com.myuniversity.app.dto.ProfesseurDTO;
import com.myuniversity.app.service.ProfesseurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professeurs")
public class ProfesseurController {

    private final ProfesseurService service;

    public ProfesseurController(ProfesseurService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProfesseurDTO> getAll() {
        return service.findAll().stream()
                .map(ProfesseurDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfesseurDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(ProfesseurDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProfesseurDTO> create(@Valid @RequestBody ProfesseurDTO dto) {
        ProfesseurDTO saved = ProfesseurDTO.fromEntity(service.save(dto.toEntity()));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfesseurDTO> update(@PathVariable Long id, @Valid @RequestBody ProfesseurDTO dto) {
        try {
            ProfesseurDTO updated = ProfesseurDTO.fromEntity(service.update(id, dto.toEntity()));
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
}
