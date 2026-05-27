package com.myuniversity.app.dto.stats;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StatsGeneralesDTO {
    private long totalEtudiants;
    private long totalProfesseurs;
    private long totalCours;
    private long totalSalles;
    private long totalInscriptions;
    private long totalPaiements;
    private long totalPaiementsEnAttente;
    private long totalPaiementsPayes;
    private long totalPaiementsEnRetard;
}
