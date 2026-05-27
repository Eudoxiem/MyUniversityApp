package com.myuniversity.app.service;

import com.myuniversity.app.entity.*;
import com.myuniversity.app.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class EmploiDuTempsService {

    private final EmploiDuTempsRepository edtRepository;
    private final CoursRepository coursRepository;
    private final SalleRepository salleRepository;
    private final ProfesseurRepository professeurRepository;

    public EmploiDuTempsService(EmploiDuTempsRepository edtRepository,
                                CoursRepository coursRepository,
                                SalleRepository salleRepository,
                                ProfesseurRepository professeurRepository) {
        this.edtRepository = edtRepository;
        this.coursRepository = coursRepository;
        this.salleRepository = salleRepository;
        this.professeurRepository = professeurRepository;
    }

    public List<EmploiDuTemps> findAll() {
        return edtRepository.findAll();
    }

    public EmploiDuTemps findById(Long id) {
        return edtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Créneau non trouvé avec l'id : " + id));
    }

    public List<EmploiDuTemps> findBySemestre(String semestre, String anneeAcademique) {
        return edtRepository.findBySemestreAndAnneeAcademique(semestre, anneeAcademique);
    }

    public List<EmploiDuTemps> findByProfesseur(Long professeurId, String semestre, String anneeAcademique) {
        return edtRepository.findByProfesseurIdAndSemestreAndAnneeAcademique(professeurId, semestre, anneeAcademique);
    }

    public List<EmploiDuTemps> findBySalle(Long salleId, String semestre, String anneeAcademique) {
        return edtRepository.findBySalleIdAndSemestreAndAnneeAcademique(salleId, semestre, anneeAcademique);
    }

    public EmploiDuTemps save(EmploiDuTemps edt, Long coursId, Long salleId, Long professeurId) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé avec l'id : " + coursId));
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'id : " + salleId));
        Professeur professeur = professeurRepository.findById(professeurId)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé avec l'id : " + professeurId));

        verifierConflits(salleId, professeurId, edt.getJourSemaine(),
                edt.getHeureDebut(), edt.getHeureFin(), edt.getSemestre(), edt.getAnneeAcademique());

        edt.setCours(cours);
        edt.setSalle(salle);
        edt.setProfesseur(professeur);
        return edtRepository.save(edt);
    }

    public EmploiDuTemps update(Long id, EmploiDuTemps edt) {
        return edtRepository.findById(id)
                .map(existing -> {
                    edt.setId(id);
                    edt.setCours(existing.getCours());
                    edt.setSalle(existing.getSalle());
                    edt.setProfesseur(existing.getProfesseur());
                    return edtRepository.save(edt);
                })
                .orElseThrow(() -> new RuntimeException("Créneau non trouvé avec l'id : " + id));
    }

    public void delete(Long id) {
        edtRepository.deleteById(id);
    }

    private void verifierConflits(Long salleId, Long professeurId, JourSemaine jour,
                                   LocalTime heureDebut, LocalTime heureFin,
                                   String semestre, String anneeAcademique) {
        List<EmploiDuTemps> conflitsSalle = edtRepository.findConflitsSalle(
                salleId, jour, heureDebut, heureFin, semestre, anneeAcademique);
        if (!conflitsSalle.isEmpty()) {
            throw new RuntimeException("Conflit : la salle est déjà occupée sur ce créneau");
        }

        List<EmploiDuTemps> conflitsProfesseur = edtRepository.findConflitsProfesseur(
                professeurId, jour, heureDebut, heureFin, semestre, anneeAcademique);
        if (!conflitsProfesseur.isEmpty()) {
            throw new RuntimeException("Conflit : le professeur est déjà occupé sur ce créneau");
        }
    }
}
