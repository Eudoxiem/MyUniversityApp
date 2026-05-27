package com.myuniversity.app.repository;

import com.myuniversity.app.entity.Paiement;
import com.myuniversity.app.entity.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    List<Paiement> findByEtudiantId(Long etudiantId);
    List<Paiement> findByStatut(StatutPaiement statut);
    List<Paiement> findByDateEcheanceBeforeAndStatut(LocalDate date, StatutPaiement statut);
}
