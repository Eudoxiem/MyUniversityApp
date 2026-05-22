package com.myuniversity.app.repository;

import com.myuniversity.app.entity.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByEtudiantId(Long etudiantId);
    List<Inscription> findByCoursId(Long coursId);
}
