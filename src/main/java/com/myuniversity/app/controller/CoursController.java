package com.myuniversity.app.controller;

import com.myuniversity.app.entity.Cours;
import com.myuniversity.app.service.CoursService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cours")
public class CoursController {

    private final CoursService service;

    public CoursController(CoursService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cours> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cours> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cours> create(@RequestBody Cours cours) {
        Cours saved = service.save(cours);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cours> update(@PathVariable Long id, @RequestBody Cours cours) {
        try {
            Cours updated = service.update(id, cours);
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
