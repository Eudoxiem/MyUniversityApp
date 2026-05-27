package com.myuniversity.app.service;

import com.myuniversity.app.entity.Etudiant;
import com.myuniversity.app.entity.Paiement;
import com.myuniversity.app.entity.StatutPaiement;
import com.myuniversity.app.repository.EtudiantRepository;
import com.myuniversity.app.repository.PaiementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final EtudiantRepository etudiantRepository;

    public PaiementService(PaiementRepository paiementRepository, EtudiantRepository etudiantRepository) {
        this.paiementRepository = paiementRepository;
        this.etudiantRepository = etudiantRepository;
    }

    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }

    public Paiement findById(Long id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'id : " + id));
    }

    public List<Paiement> findByEtudiantId(Long etudiantId) {
        return paiementRepository.findByEtudiantId(etudiantId);
    }

    public Paiement save(Paiement paiement, Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'id : " + etudiantId));
        paiement.setEtudiant(etudiant);
        return paiementRepository.save(paiement);
    }

    public Paiement update(Long id, Paiement paiement) {
        return paiementRepository.findById(id)
                .map(existing -> {
                    paiement.setId(id);
                    paiement.setEtudiant(existing.getEtudiant());
                    return paiementRepository.save(paiement);
                })
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'id : " + id));
    }

    public void delete(Long id) {
        paiementRepository.deleteById(id);
    }

    public List<Paiement> findPaiementsEnRetard() {
        return paiementRepository.findByDateEcheanceBeforeAndStatut(
                LocalDate.now(), StatutPaiement.EN_ATTENTE);
    }
}
