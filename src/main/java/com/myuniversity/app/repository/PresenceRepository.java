package com.myuniversity.app.repository;

import com.myuniversity.app.entity.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

    List<Presence> findByCoursIdAndDateOrderByEtudiantNom(Long coursId, LocalDate date);

    List<Presence> findByEtudiantIdOrderByDateDesc(Long etudiantId);

    List<Presence> findByEtudiantIdAndCoursIdOrderByDateDesc(Long etudiantId, Long coursId);

    Optional<Presence> findByEtudiantIdAndCoursIdAndDate(Long etudiantId, Long coursId, LocalDate date);

    long countByCoursIdAndDateAndPresentTrue(Long coursId, LocalDate date);

    long countByCoursIdAndDate(Long coursId, LocalDate date);
}
