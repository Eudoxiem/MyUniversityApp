package com.myuniversity.app.dto;

import com.myuniversity.app.entity.Grade;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GradeDTO {

    private Long id;

    @NotNull(message = "La valeur finale est obligatoire")
    private Double valeurFinale;

    private String mention;

    private LocalDate dateValidation;

    @NotNull(message = "L'ID de l'inscription est obligatoire")
    private Long inscriptionId;

    public static GradeDTO fromEntity(Grade grade) {
        return GradeDTO.builder()
                .id(grade.getId())
                .valeurFinale(grade.getValeurFinale())
                .mention(grade.getMention())
                .dateValidation(grade.getDateValidation())
                .inscriptionId(grade.getInscription() != null ? grade.getInscription().getId() : null)
                .build();
    }
}
