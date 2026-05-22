package com.myuniversity.app.controller;

import com.myuniversity.app.dto.CoursDTO;
import com.myuniversity.app.entity.Cours;
import com.myuniversity.app.repository.ProfesseurRepository;
import com.myuniversity.app.repository.SalleRepository;
import com.myuniversity.app.service.CoursService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<CoursDTO> getAll() {
        return service.findAll().stream()
                .map(CoursDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoursDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(CoursDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CoursDTO> create(@Valid @RequestBody CoursDTO dto) {
        Cours cours = mapToEntity(dto);
        CoursDTO saved = CoursDTO.fromEntity(service.save(cours));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoursDTO> update(@PathVariable Long id, @Valid @RequestBody CoursDTO dto) {
        try {
            Cours cours = mapToEntity(dto);
            CoursDTO updated = CoursDTO.fromEntity(service.update(id, cours));
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

    private Cours mapToEntity(CoursDTO dto) {
        Cours cours = new Cours();
        cours.setId(dto.getId());
        cours.setCode(dto.getCode());
        cours.setNom(dto.getNom());
        cours.setCredits(dto.getCredits());
        cours.setDescription(dto.getDescription());
        if (dto.getProfesseurId() != null) {
            professeurRepository.findById(dto.getProfesseurId())
                    .ifPresent(cours::setProfesseur);
        }
        if (dto.getSalleId() != null) {
            salleRepository.findById(dto.getSalleId())
                    .ifPresent(cours::setSalle);
        }
        return cours;
    }
}
