package com.myuniversity.app.service;

import com.myuniversity.app.entity.Cours;
import com.myuniversity.app.repository.CoursRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoursService {

    private final CoursRepository repository;

    public CoursService(CoursRepository repository) {
        this.repository = repository;
    }

    public List<Cours> findAll() {
        return repository.findAll();
    }

    public Optional<Cours> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Cours> findByCode(String code) {
        return repository.findByCode(code);
    }

    public List<Cours> findByProfesseurId(Long professeurId) {
        return repository.findByProfesseurId(professeurId);
    }

    public Cours save(Cours cours) {
        return repository.save(cours);
    }

    public Cours update(Long id, Cours cours) {
        return repository.findById(id)
                .map(existing -> {
                    cours.setId(id);
                    return repository.save(cours);
                })
                .orElseThrow(() -> new RuntimeException("Cours non trouvé avec l'id : " + id));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
