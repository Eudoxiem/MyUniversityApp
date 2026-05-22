package com.myuniversity.app.controller;

import com.myuniversity.app.entity.Inscription;
import com.myuniversity.app.service.InscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionController {

    private final InscriptionService service;

    public InscriptionController(InscriptionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Inscription> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inscription> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Inscription> create(@RequestBody Inscription inscription) {
        Inscription saved = service.save(inscription);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inscription> update(@PathVariable Long id, @RequestBody Inscription inscription) {
        try {
            Inscription updated = service.update(id, inscription);
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
