package com.myuniversity.app.controller;

import com.myuniversity.app.dto.SalleDTO;
import com.myuniversity.app.service.SalleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salles")
public class SalleController {

    private final SalleService service;

    public SalleController(SalleService service) {
        this.service = service;
    }

    @GetMapping
    public List<SalleDTO> getAll() {
        return service.findAll().stream()
                .map(SalleDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalleDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(SalleDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SalleDTO> create(@Valid @RequestBody SalleDTO dto) {
        SalleDTO saved = SalleDTO.fromEntity(service.save(dto.toEntity()));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalleDTO> update(@PathVariable Long id, @Valid @RequestBody SalleDTO dto) {
        try {
            SalleDTO updated = SalleDTO.fromEntity(service.update(id, dto.toEntity()));
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
