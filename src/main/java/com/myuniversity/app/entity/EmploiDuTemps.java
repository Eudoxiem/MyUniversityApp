package com.myuniversity.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "emploi_du_temps", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"salle_id", "jour_semaine", "heure_debut", "semestre", "annee_academique"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EmploiDuTemps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professeur_id", nullable = false)
    private Professeur professeur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JourSemaine jourSemaine;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    @Column(nullable = false)
    private String semestre;

    @Column(nullable = false)
    private String anneeAcademique;
}
