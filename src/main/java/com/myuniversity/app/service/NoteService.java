package com.myuniversity.app.service;

import com.myuniversity.app.entity.Note;
import com.myuniversity.app.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoteService {

    private final NoteRepository repository;

    public NoteService(NoteRepository repository) {
        this.repository = repository;
    }

    public List<Note> findAll() {
        return repository.findAll();
    }

    public Optional<Note> findById(Long id) {
        return repository.findById(id);
    }

    public List<Note> findByInscriptionId(Long inscriptionId) {
        return repository.findByInscriptionId(inscriptionId);
    }

    public Note save(Note note) {
        return repository.save(note);
    }

    public Note update(Long id, Note note) {
        return repository.findById(id)
                .map(existing -> {
                    note.setId(id);
                    return repository.save(note);
                })
                .orElseThrow(() -> new RuntimeException("Note non trouvée avec l'id : " + id));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
