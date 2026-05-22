package com.myuniversity.app.controller;

import com.myuniversity.app.dto.EtudiantDTO;
import com.myuniversity.app.service.EtudiantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final EtudiantService service;

    public EtudiantController(EtudiantService service) {
        this.service = service;
    }

    @GetMapping
    public List<EtudiantDTO> getAll() {
        return service.findAll().stream()
                .map(EtudiantDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EtudiantDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(EtudiantDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EtudiantDTO> create(@Valid @RequestBody EtudiantDTO dto) {
        EtudiantDTO saved = EtudiantDTO.fromEntity(service.save(dto.toEntity()));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EtudiantDTO> update(@PathVariable Long id, @Valid @RequestBody EtudiantDTO dto) {
        try {
            EtudiantDTO updated = EtudiantDTO.fromEntity(service.update(id, dto.toEntity()));
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
