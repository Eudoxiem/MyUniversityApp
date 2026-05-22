package com.myuniversity.app.dto;

import com.myuniversity.app.entity.Cours;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CoursDTO {

    private Long id;

    @NotBlank(message = "Le code du cours est obligatoire")
    private String code;

    @NotBlank(message = "Le nom du cours est obligatoire")
    private String nom;

    private Integer credits;

    private String description;

    private Long professeurId;

    private Long salleId;

    public static CoursDTO fromEntity(Cours cours) {
        return CoursDTO.builder()
                .id(cours.getId())
                .code(cours.getCode())
                .nom(cours.getNom())
                .credits(cours.getCredits())
                .description(cours.getDescription())
                .professeurId(cours.getProfesseur() != null ? cours.getProfesseur().getId() : null)
                .salleId(cours.getSalle() != null ? cours.getSalle().getId() : null)
                .build();
    }
}
