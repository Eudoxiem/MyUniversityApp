package com.myuniversity.app.controller;

import com.myuniversity.app.entity.Professeur;
import com.myuniversity.app.service.ProfesseurService;
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
    public List<Professeur> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professeur> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Professeur> create(@RequestBody Professeur professeur) {
        Professeur saved = service.save(professeur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professeur> update(@PathVariable Long id, @RequestBody Professeur professeur) {
        try {
            Professeur updated = service.update(id, professeur);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(existing -> {
                    service.delete(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
