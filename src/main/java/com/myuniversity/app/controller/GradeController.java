package com.myuniversity.app.controller;

import com.myuniversity.app.dto.GradeDTO;
import com.myuniversity.app.entity.Grade;
import com.myuniversity.app.repository.InscriptionRepository;
import com.myuniversity.app.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService service;
    private final InscriptionRepository inscriptionRepository;

    public GradeController(GradeService service, InscriptionRepository inscriptionRepository) {
        this.service = service;
        this.inscriptionRepository = inscriptionRepository;
    }

    @GetMapping
    public List<GradeDTO> getAll() {
        return service.findAll().stream()
                .map(GradeDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(GradeDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GradeDTO> create(@Valid @RequestBody GradeDTO dto) {
        Grade grade = mapToEntity(dto);
        GradeDTO saved = GradeDTO.fromEntity(service.save(grade));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeDTO> update(@PathVariable Long id, @Valid @RequestBody GradeDTO dto) {
        try {
            Grade grade = mapToEntity(dto);
            GradeDTO updated = GradeDTO.fromEntity(service.update(id, grade));
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

    private Grade mapToEntity(GradeDTO dto) {
        Grade grade = new Grade();
        grade.setId(dto.getId());
        grade.setValeurFinale(dto.getValeurFinale());
        grade.setMention(dto.getMention());
        grade.setDateValidation(dto.getDateValidation());
        inscriptionRepository.findById(dto.getInscriptionId()).ifPresent(grade::setInscription);
        return grade;
    }
}
