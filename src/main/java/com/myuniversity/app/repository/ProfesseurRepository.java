package com.myuniversity.app.repository;

import com.myuniversity.app.entity.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfesseurRepository extends JpaRepository<Professeur, Long> {
    Optional<Professeur> findByNumeroEmploye(String numeroEmploye);
    Optional<Professeur> findByEmail(String email);
}
