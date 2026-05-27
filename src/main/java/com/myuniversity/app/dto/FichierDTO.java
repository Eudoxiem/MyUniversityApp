package com.myuniversity.app.dto;

import com.myuniversity.app.entity.Fichier;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FichierDTO {

    private Long id;
    private String nomOriginal;
    private String typeMime;
    private Long taille;
    private LocalDateTime dateUpload;
    private Long entiteId;
    private String entiteType;

    public static FichierDTO fromEntity(Fichier fichier) {
        return FichierDTO.builder()
                .id(fichier.getId())
                .nomOriginal(fichier.getNomOriginal())
                .typeMime(fichier.getTypeMime())
                .taille(fichier.getTaille())
                .dateUpload(fichier.getDateUpload())
                .entiteId(fichier.getEntiteId())
                .entiteType(fichier.getEntiteType())
                .build();
    }
}
