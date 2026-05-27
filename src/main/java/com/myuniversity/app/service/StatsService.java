package com.myuniversity.app.service;

import com.myuniversity.app.dto.stats.StatsCoursDTO;
import com.myuniversity.app.dto.stats.StatsGeneralesDTO;
import com.myuniversity.app.entity.*;
import com.myuniversity.app.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsService {

    private final EtudiantRepository etudiantRepository;
    private final ProfesseurRepository professeurRepository;
    private final CoursRepository coursRepository;
    private final SalleRepository salleRepository;
    private final InscriptionRepository inscriptionRepository;
    private final PaiementRepository paiementRepository;
    private final NoteRepository noteRepository;

    public StatsService(EtudiantRepository etudiantRepository,
                        ProfesseurRepository professeurRepository,
                        CoursRepository coursRepository,
                        SalleRepository salleRepository,
                        InscriptionRepository inscriptionRepository,
                        PaiementRepository paiementRepository,
                        NoteRepository noteRepository) {
        this.etudiantRepository = etudiantRepository;
        this.professeurRepository = professeurRepository;
        this.coursRepository = coursRepository;
        this.salleRepository = salleRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.paiementRepository = paiementRepository;
        this.noteRepository = noteRepository;
    }

    public StatsGeneralesDTO getStatsGenerales() {
        return StatsGeneralesDTO.builder()
                .totalEtudiants(etudiantRepository.count())
                .totalProfesseurs(professeurRepository.count())
                .totalCours(coursRepository.count())
                .totalSalles(salleRepository.count())
                .totalInscriptions(inscriptionRepository.count())
                .totalPaiements(paiementRepository.count())
                .totalPaiementsEnAttente(paiementRepository.findByStatut(StatutPaiement.EN_ATTENTE).size())
                .totalPaiementsPayes(paiementRepository.findByStatut(StatutPaiement.PAYE).size())
                .totalPaiementsEnRetard(paiementRepository.findByStatut(StatutPaiement.EN_RETARD).size())
                .build();
    }

    public List<StatsCoursDTO> getStatsCours() {
        List<Cours> coursList = coursRepository.findAll();

        return coursList.stream().map(cours -> {
            List<Inscription> inscriptions = inscriptionRepository.findByCoursId(cours.getId());
            List<Note> notes = noteRepository.findByInscription_Cours_Id(cours.getId());

            double moyenne = notes.stream()
                    .mapToDouble(Note::getValeur)
                    .average()
                    .orElse(0.0);

            return StatsCoursDTO.builder()
                    .coursId(cours.getId())
                    .coursNom(cours.getNom())
                    .code(cours.getCode())
                    .nombreEtudiants(inscriptions.size())
                    .moyenneGenerale(Math.round(moyenne * 100.0) / 100.0)
                    .totalInscriptions(inscriptions.size())
                    .build();
        }).toList();
    }
}
