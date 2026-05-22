package com.myuniversity.app.controller;

import com.myuniversity.app.entity.Grade;
import com.myuniversity.app.service.GradeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService service;

    public GradeController(GradeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Grade> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grade> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Grade> create(@RequestBody Grade grade) {
        Grade saved = service.save(grade);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grade> update(@PathVariable Long id, @RequestBody Grade grade) {
        try {
            Grade updated = service.update(id, grade);
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
