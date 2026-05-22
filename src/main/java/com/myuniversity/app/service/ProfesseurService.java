package com.myuniversity.app.service;

import com.myuniversity.app.entity.Professeur;
import com.myuniversity.app.repository.ProfesseurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProfesseurService {

    private final ProfesseurRepository repository;

    public ProfesseurService(ProfesseurRepository repository) {
        this.repository = repository;
    }

    public List<Professeur> findAll() {
        return repository.findAll();
    }

    public Optional<Professeur> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Professeur> findByNumeroEmploye(String numeroEmploye) {
        return repository.findByNumeroEmploye(numeroEmploye);
    }

    public Professeur save(Professeur professeur) {
        return repository.save(professeur);
    }

    public Professeur update(Long id, Professeur professeur) {
        return repository.findById(id)
                .map(existing -> {
                    professeur.setId(id);
                    return repository.save(professeur);
                })
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'id : " + id));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
