package com.myuniversity.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "paiements")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private LocalDate datePaiement;

    private LocalDate dateEcheance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModePaiement modePaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    private String description;
}
