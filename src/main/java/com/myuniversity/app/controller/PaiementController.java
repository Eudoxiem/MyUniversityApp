package com.myuniversity.app.controller;

import com.myuniversity.app.dto.PaiementDTO;
import com.myuniversity.app.dto.paiement.PaymentIntentResponse;
import com.myuniversity.app.entity.Paiement;
import com.myuniversity.app.service.OnlinePaymentService;
import com.myuniversity.app.service.PaiementService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;
    private final OnlinePaymentService onlinePaymentService;

    public PaiementController(PaiementService paiementService, OnlinePaymentService onlinePaymentService) {
        this.paiementService = paiementService;
        this.onlinePaymentService = onlinePaymentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public List<PaiementDTO> getAll() {
        return paiementService.findAll().stream()
                .map(PaiementDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR') or @securityHelper.estProprietairePaiement(#id)")
    public ResponseEntity<PaiementDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(PaiementDTO.fromEntity(paiementService.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR') or @securityHelper.estProprietairePaiementByEtudiant(#etudiantId)")
    public List<PaiementDTO> getByEtudiant(@PathVariable Long etudiantId) {
        return paiementService.findByEtudiantId(etudiantId).stream()
                .map(PaiementDTO::fromEntity)
                .toList();
    }

    @GetMapping("/en-retard")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaiementDTO> getPaiementsEnRetard() {
        return paiementService.findPaiementsEnRetard().stream()
                .map(PaiementDTO::fromEntity)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaiementDTO> create(@Valid @RequestBody PaiementDTO dto) {
        try {
            PaiementDTO saved = PaiementDTO.fromEntity(
                    paiementService.save(dto.toEntity(), dto.getEtudiantId()));
            log.info("Paiement créé - id: {}, étudiantId: {}, montant: {}", saved.getId(), dto.getEtudiantId(), saved.getMontant());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            log.warn("Échec création paiement - étudiantId: {}", dto.getEtudiantId());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaiementDTO> update(@PathVariable Long id, @Valid @RequestBody PaiementDTO dto) {
        try {
            PaiementDTO updated = PaiementDTO.fromEntity(paiementService.update(id, dto.toEntity()));
            log.info("Paiement modifié - id: {}", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Paiement non trouvé pour modification - id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/payer-en-ligne")
    @PreAuthorize("hasAnyRole('ADMIN', 'ETUDIANT')")
    public ResponseEntity<PaymentIntentResponse> initierPaiementEnLigne(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(onlinePaymentService.initierPaiement(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/confirmer-paiement")
    @PreAuthorize("hasAnyRole('ADMIN', 'ETUDIANT')")
    public ResponseEntity<PaiementDTO> confirmerPaiement(@RequestParam String paymentIntentId) {
        try {
            Paiement paiement = onlinePaymentService.confirmerPaiement(paymentIntentId);
            return ResponseEntity.ok(PaiementDTO.fromEntity(paiement));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            paiementService.findById(id);
            paiementService.delete(id);
            log.info("Paiement supprimé - id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Paiement non trouvé pour suppression - id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
