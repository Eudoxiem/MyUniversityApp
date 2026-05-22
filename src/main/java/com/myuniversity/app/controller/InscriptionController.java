package com.myuniversity.app.controller;

import com.myuniversity.app.dto.InscriptionDTO;
import com.myuniversity.app.entity.Inscription;
import com.myuniversity.app.repository.CoursRepository;
import com.myuniversity.app.repository.EtudiantRepository;
import com.myuniversity.app.service.InscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<InscriptionDTO> getAll() {
        return service.findAll().stream()
                .map(InscriptionDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscriptionDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok(InscriptionDTO.fromEntity(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InscriptionDTO> create(@Valid @RequestBody InscriptionDTO dto) {
        Inscription inscription = mapToEntity(dto);
        InscriptionDTO saved = InscriptionDTO.fromEntity(service.save(inscription));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InscriptionDTO> update(@PathVariable Long id, @Valid @RequestBody InscriptionDTO dto) {
        try {
            Inscription inscription = mapToEntity(dto);
            InscriptionDTO updated = InscriptionDTO.fromEntity(service.update(id, inscription));
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

    private Inscription mapToEntity(InscriptionDTO dto) {
        Inscription inscription = new Inscription();
        inscription.setId(dto.getId());
        inscription.setDateInscription(dto.getDateInscription());
        etudiantRepository.findById(dto.getEtudiantId()).ifPresent(inscription::setEtudiant);
        coursRepository.findById(dto.getCoursId()).ifPresent(inscription::setCours);
        return inscription;
    }
}
