package com.myuniversity.app.dto.stats;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StatsCoursDTO {
    private Long coursId;
    private String coursNom;
    private String code;
    private long nombreEtudiants;
    private Double moyenneGenerale;
    private int totalInscriptions;
}
