package com.myuniversity.app.controller;

import com.myuniversity.app.entity.Inscription;
import com.myuniversity.app.repository.InscriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionController {

    private final InscriptionRepository repository;

    public InscriptionController(InscriptionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Inscription> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inscription> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Inscription> create(@RequestBody Inscription inscription) {
        Inscription saved = repository.save(inscription);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inscription> update(@PathVariable Long id, @RequestBody Inscription inscription) {
        return repository.findById(id)
                .map(existing -> {
                    inscription.setId(id);
                    return ResponseEntity.ok(repository.save(inscription));
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
