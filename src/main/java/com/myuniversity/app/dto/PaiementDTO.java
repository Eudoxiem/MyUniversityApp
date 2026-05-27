package com.myuniversity.app.dto;

import com.myuniversity.app.entity.ModePaiement;
import com.myuniversity.app.entity.Paiement;
import com.myuniversity.app.entity.StatutPaiement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PaiementDTO {

    private Long id;

    @NotBlank(message = "La référence est obligatoire")
    private String reference;

    @NotNull(message = "L'ID de l'étudiant est obligatoire")
    private Long etudiantId;

    private String etudiantNom;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double montant;

    @NotNull(message = "La date de paiement est obligatoire")
    private LocalDate datePaiement;

    private LocalDate dateEcheance;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    @Builder.Default
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    private String description;

    public static PaiementDTO fromEntity(Paiement paiement) {
        return PaiementDTO.builder()
                .id(paiement.getId())
                .reference(paiement.getReference())
                .etudiantId(paiement.getEtudiant().getId())
                .etudiantNom(paiement.getEtudiant().getNom() + " " + paiement.getEtudiant().getPrenom())
                .montant(paiement.getMontant())
                .datePaiement(paiement.getDatePaiement())
                .dateEcheance(paiement.getDateEcheance())
                .modePaiement(paiement.getModePaiement())
                .statut(paiement.getStatut())
                .description(paiement.getDescription())
                .build();
    }

    public Paiement toEntity() {
        return Paiement.builder()
                .id(id)
                .reference(reference)
                .montant(montant)
                .datePaiement(datePaiement)
                .dateEcheance(dateEcheance)
                .modePaiement(modePaiement)
                .statut(statut)
                .description(description)
                .build();
    }
}
