package com.myuniversity.app.service;

import com.myuniversity.app.entity.Etudiant;
import com.myuniversity.app.repository.EtudiantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EtudiantService {

    private final EtudiantRepository repository;

    public EtudiantService(EtudiantRepository repository) {
        this.repository = repository;
    }

    public List<Etudiant> findAll() {
        return repository.findAll();
    }

    public Optional<Etudiant> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Etudiant> findByMatricule(String matricule) {
        return repository.findByMatricule(matricule);
    }

    public Etudiant save(Etudiant etudiant) {
        return repository.save(etudiant);
    }

    public Etudiant update(Long id, Etudiant etudiant) {
        return repository.findById(id)
                .map(existing -> {
                    etudiant.setId(id);
                    return repository.save(etudiant);
                })
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'id : " + id));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
