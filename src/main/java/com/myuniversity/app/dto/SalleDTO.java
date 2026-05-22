package com.myuniversity.app.dto;

import com.myuniversity.app.entity.Salle;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SalleDTO {

    private Long id;

    @NotBlank(message = "Le code de la salle est obligatoire")
    private String code;

    @NotBlank(message = "Le nom de la salle est obligatoire")
    private String nom;

    private Integer capacite;

    private String batiment;

    public static SalleDTO fromEntity(Salle salle) {
        return SalleDTO.builder()
                .id(salle.getId())
                .code(salle.getCode())
                .nom(salle.getNom())
                .capacite(salle.getCapacite())
                .batiment(salle.getBatiment())
                .build();
    }

    public Salle toEntity() {
        return Salle.builder()
                .id(id)
                .code(code)
                .nom(nom)
                .capacite(capacite)
                .batiment(batiment)
                .build();
    }
}
