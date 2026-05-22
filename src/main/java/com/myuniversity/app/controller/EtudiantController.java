package com.myuniversity.app.controller;

import com.myuniversity.app.entity.Etudiant;
import com.myuniversity.app.service.EtudiantService;
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
    public List<Etudiant> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Etudiant> create(@RequestBody Etudiant etudiant) {
        Etudiant saved = service.save(etudiant);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> update(@PathVariable Long id, @RequestBody Etudiant etudiant) {
        try {
            Etudiant updated = service.update(id, etudiant);
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
