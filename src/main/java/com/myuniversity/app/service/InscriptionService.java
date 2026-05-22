package com.myuniversity.app.service;

import com.myuniversity.app.entity.Inscription;
import com.myuniversity.app.repository.InscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InscriptionService {

    private final InscriptionRepository repository;

    public InscriptionService(InscriptionRepository repository) {
        this.repository = repository;
    }

    public List<Inscription> findAll() {
        return repository.findAll();
    }

    public Optional<Inscription> findById(Long id) {
        return repository.findById(id);
    }

    public List<Inscription> findByEtudiantId(Long etudiantId) {
        return repository.findByEtudiantId(etudiantId);
    }

    public List<Inscription> findByCoursId(Long coursId) {
        return repository.findByCoursId(coursId);
    }

    public Inscription save(Inscription inscription) {
        return repository.save(inscription);
    }

    public Inscription update(Long id, Inscription inscription) {
        return repository.findById(id)
                .map(existing -> {
                    inscription.setId(id);
                    return repository.save(inscription);
                })
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée avec l'id : " + id));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
