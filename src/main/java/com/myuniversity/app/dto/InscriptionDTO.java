package com.myuniversity.app.dto;

import com.myuniversity.app.entity.Inscription;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class InscriptionDTO {

    private Long id;

    @NotNull(message = "La date d'inscription est obligatoire")
    private LocalDate dateInscription;

    @NotNull(message = "L'ID de l'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "L'ID du cours est obligatoire")
    private Long coursId;

    public static InscriptionDTO fromEntity(Inscription inscription) {
        return InscriptionDTO.builder()
                .id(inscription.getId())
                .dateInscription(inscription.getDateInscription())
                .etudiantId(inscription.getEtudiant() != null ? inscription.getEtudiant().getId() : null)
                .coursId(inscription.getCours() != null ? inscription.getCours().getId() : null)
                .build();
    }
}
