package com.myuniversity.app.service;

import com.myuniversity.app.entity.Grade;
import com.myuniversity.app.repository.GradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GradeService {

    private final GradeRepository repository;

    public GradeService(GradeRepository repository) {
        this.repository = repository;
    }

    public List<Grade> findAll() {
        return repository.findAll();
    }

    public Optional<Grade> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Grade> findByInscriptionId(Long inscriptionId) {
        return repository.findByInscriptionId(inscriptionId);
    }

    public Grade save(Grade grade) {
        return repository.save(grade);
    }

    public Grade update(Long id, Grade grade) {
        return repository.findById(id)
                .map(existing -> {
                    grade.setId(id);
                    return repository.save(grade);
                })
                .orElseThrow(() -> new RuntimeException("Grade non trouvé avec l'id : " + id));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
