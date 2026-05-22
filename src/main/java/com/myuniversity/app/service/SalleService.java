package com.myuniversity.app.service;

import com.myuniversity.app.entity.Salle;
import com.myuniversity.app.repository.SalleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SalleService {

    private final SalleRepository repository;

    public SalleService(SalleRepository repository) {
        this.repository = repository;
    }

    public List<Salle> findAll() {
        return repository.findAll();
    }

    public Optional<Salle> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Salle> findByCode(String code) {
        return repository.findByCode(code);
    }

    public Salle save(Salle salle) {
        return repository.save(salle);
    }

    public Salle update(Long id, Salle salle) {
        return repository.findById(id)
                .map(existing -> {
                    salle.setId(id);
                    return repository.save(salle);
                })
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'id : " + id));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
