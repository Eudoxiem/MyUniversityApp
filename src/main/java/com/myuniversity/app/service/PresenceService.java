package com.myuniversity.app.service;

import com.myuniversity.app.dto.presence.AppelRequest;
import com.myuniversity.app.dto.presence.PresenceDTO;
import com.myuniversity.app.dto.presence.PresenceRequest;
import com.myuniversity.app.entity.Cours;
import com.myuniversity.app.entity.Etudiant;
import com.myuniversity.app.entity.Presence;
import com.myuniversity.app.repository.CoursRepository;
import com.myuniversity.app.repository.EtudiantRepository;
import com.myuniversity.app.repository.PresenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional
public class PresenceService {

    private final PresenceRepository presenceRepository;
    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;

    public PresenceService(PresenceRepository presenceRepository,
                           EtudiantRepository etudiantRepository,
                           CoursRepository coursRepository) {
        this.presenceRepository = presenceRepository;
        this.etudiantRepository = etudiantRepository;
        this.coursRepository = coursRepository;
    }

    public PresenceDTO getPresenceById(Long id) {
        return presenceRepository.findById(id)
                .map(PresenceDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Présence introuvable"));
    }

    public List<PresenceDTO> getPresencesByCoursAndDate(Long coursId, LocalDate date) {
        return presenceRepository.findByCoursIdAndDateOrderByEtudiantNom(coursId, date).stream()
                .map(PresenceDTO::fromEntity)
                .toList();
    }

    public List<PresenceDTO> getPresencesByEtudiant(Long etudiantId) {
        return presenceRepository.findByEtudiantIdOrderByDateDesc(etudiantId).stream()
                .map(PresenceDTO::fromEntity)
                .toList();
    }

    public List<PresenceDTO> getPresencesByEtudiantAndCours(Long etudiantId, Long coursId) {
        return presenceRepository.findByEtudiantIdAndCoursIdOrderByDateDesc(etudiantId, coursId).stream()
                .map(PresenceDTO::fromEntity)
                .toList();
    }

    public PresenceDTO createPresence(PresenceRequest request) {
        Etudiant etudiant = etudiantRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));
        Cours cours = coursRepository.findById(request.getCoursId())
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));

        if (presenceRepository.findByEtudiantIdAndCoursIdAndDate(
                request.getEtudiantId(), request.getCoursId(), request.getDate()).isPresent()) {
            throw new RuntimeException("Une présence existe déjà pour cet étudiant à cette date");
        }

        Presence presence = Presence.builder()
                .etudiant(etudiant)
                .cours(cours)
                .date(request.getDate())
                .present(request.isPresent())
                .justifie(request.isJustifie())
                .justification(request.getJustification())
                .build();

        presence = presenceRepository.save(presence);
        log.info("Présence créée - étudiantId: {}, coursId: {}, date: {}, présent: {}",
                request.getEtudiantId(), request.getCoursId(), request.getDate(), request.isPresent());
        return PresenceDTO.fromEntity(presence);
    }

    public List<PresenceDTO> faireAppel(AppelRequest request) {
        Cours cours = coursRepository.findById(request.getCoursId())
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));

        List<PresenceDTO> resultats = request.getPresences().stream().map(p -> {
            Etudiant etudiant = etudiantRepository.findById(p.getEtudiantId())
                    .orElseThrow(() -> new RuntimeException("Étudiant introuvable: " + p.getEtudiantId()));

            Presence presence = presenceRepository
                    .findByEtudiantIdAndCoursIdAndDate(p.getEtudiantId(), request.getCoursId(), request.getDate())
                    .orElseGet(() -> Presence.builder()
                            .etudiant(etudiant)
                            .cours(cours)
                            .date(request.getDate())
                            .build());

            presence.setPresent(p.isPresent());
            if (p.isPresent()) {
                presence.setJustifie(false);
                presence.setJustification(null);
            }

            return PresenceDTO.fromEntity(presenceRepository.save(presence));
        }).toList();

        log.info("Appel effectué - coursId: {}, date: {}, {} étudiants",
                request.getCoursId(), request.getDate(), resultats.size());
        return resultats;
    }

    public PresenceDTO updatePresence(Long id, PresenceRequest request) {
        Presence presence = presenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Présence introuvable"));

        presence.setPresent(request.isPresent());
        presence.setJustifie(request.isJustifie());
        presence.setJustification(request.getJustification());
        presence = presenceRepository.save(presence);

        log.info("Présence modifiée - id: {}, présent: {}, justifié: {}", id, request.isPresent(), request.isJustifie());
        return PresenceDTO.fromEntity(presence);
    }

    public void deletePresence(Long id) {
        presenceRepository.deleteById(id);
        log.info("Présence supprimée - id: {}", id);
    }
}
