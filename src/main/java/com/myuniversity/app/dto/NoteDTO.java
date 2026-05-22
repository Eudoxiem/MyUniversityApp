package com.myuniversity.app.dto;

import com.myuniversity.app.entity.Note;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NoteDTO {

    private Long id;

    @NotNull(message = "La valeur est obligatoire")
    private Double valeur;

    private Double coefficient;

    private String type;

    private LocalDate dateSaisie;

    @NotNull(message = "L'ID de l'inscription est obligatoire")
    private Long inscriptionId;

    public static NoteDTO fromEntity(Note note) {
        return NoteDTO.builder()
                .id(note.getId())
                .valeur(note.getValeur())
                .coefficient(note.getCoefficient())
                .type(note.getType())
                .dateSaisie(note.getDateSaisie())
                .inscriptionId(note.getInscription() != null ? note.getInscription().getId() : null)
                .build();
    }
}
