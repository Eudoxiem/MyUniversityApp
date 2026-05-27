package com.myuniversity.app.repository;

import com.myuniversity.app.entity.EmploiDuTemps;
import com.myuniversity.app.entity.JourSemaine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface EmploiDuTempsRepository extends JpaRepository<EmploiDuTemps, Long> {

    List<EmploiDuTemps> findByProfesseurIdAndSemestreAndAnneeAcademique(
            Long professeurId, String semestre, String anneeAcademique);

    List<EmploiDuTemps> findBySalleIdAndSemestreAndAnneeAcademique(
            Long salleId, String semestre, String anneeAcademique);

    List<EmploiDuTemps> findBySemestreAndAnneeAcademique(String semestre, String anneeAcademique);

    @Query("SELECT e FROM EmploiDuTemps e WHERE e.salle.id = :salleId " +
           "AND e.jourSemaine = :jour AND e.semestre = :semestre AND e.anneeAcademique = :annee " +
           "AND e.heureDebut < :heureFin AND e.heureFin > :heureDebut")
    List<EmploiDuTemps> findConflitsSalle(
            @Param("salleId") Long salleId,
            @Param("jour") JourSemaine jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin,
            @Param("semestre") String semestre,
            @Param("annee") String anneeAcademique);

    @Query("SELECT e FROM EmploiDuTemps e WHERE e.professeur.id = :professeurId " +
           "AND e.jourSemaine = :jour AND e.semestre = :semestre AND e.anneeAcademique = :annee " +
           "AND e.heureDebut < :heureFin AND e.heureFin > :heureDebut")
    List<EmploiDuTemps> findConflitsProfesseur(
            @Param("professeurId") Long professeurId,
            @Param("jour") JourSemaine jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin,
            @Param("semestre") String semestre,
            @Param("annee") String anneeAcademique);
}
