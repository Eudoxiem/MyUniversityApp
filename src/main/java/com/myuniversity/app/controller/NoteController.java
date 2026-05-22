package com.myuniversity.app.controller;

import com.myuniversity.app.entity.Note;
import com.myuniversity.app.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Note> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Note> create(@RequestBody Note note) {
        Note saved = service.save(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> update(@PathVariable Long id, @RequestBody Note note) {
        try {
            Note updated = service.update(id, note);
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
