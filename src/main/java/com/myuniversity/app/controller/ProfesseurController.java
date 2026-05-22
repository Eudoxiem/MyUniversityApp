package com.myuniversity.app.controller;

import com.myuniversity.app.entity.Professeur;
import com.myuniversity.app.repository.ProfesseurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professeurs")
public class ProfesseurController {

    private final ProfesseurRepository repository;

    public ProfesseurController(ProfesseurRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Professeur> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professeur> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Professeur> create(@RequestBody Professeur professeur) {
        Professeur saved = repository.save(professeur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professeur> update(@PathVariable Long id, @RequestBody Professeur professeur) {
        return repository.findById(id)
                .map(existing -> {
                    professeur.setId(id);
                    return ResponseEntity.ok(repository.save(professeur));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
