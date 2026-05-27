package com.myuniversity.app.dto;

import com.myuniversity.app.entity.EmploiDuTemps;
import com.myuniversity.app.entity.JourSemaine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EmploiDuTempsDTO {

    private Long id;

    @NotNull(message = "Le cours est obligatoire")
    private Long coursId;

    private String coursNom;

    @NotNull(message = "La salle est obligatoire")
    private Long salleId;

    private String salleNom;

    @NotNull(message = "Le professeur est obligatoire")
    private Long professeurId;

    private String professeurNom;

    @NotNull(message = "Le jour est obligatoire")
    private JourSemaine jourSemaine;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    @NotBlank(message = "Le semestre est obligatoire")
    private String semestre;

    @NotBlank(message = "L'année académique est obligatoire")
    private String anneeAcademique;

    public static EmploiDuTempsDTO fromEntity(EmploiDuTemps edt) {
        return EmploiDuTempsDTO.builder()
                .id(edt.getId())
                .coursId(edt.getCours().getId())
                .coursNom(edt.getCours().getNom())
                .salleId(edt.getSalle().getId())
                .salleNom(edt.getSalle().getNom())
                .professeurId(edt.getProfesseur().getId())
                .professeurNom(edt.getProfesseur().getNom() + " " + edt.getProfesseur().getPrenom())
                .jourSemaine(edt.getJourSemaine())
                .heureDebut(edt.getHeureDebut())
                .heureFin(edt.getHeureFin())
                .semestre(edt.getSemestre())
                .anneeAcademique(edt.getAnneeAcademique())
                .build();
    }

    public EmploiDuTemps toEntity() {
        return EmploiDuTemps.builder()
                .id(id)
                .jourSemaine(jourSemaine)
                .heureDebut(heureDebut)
                .heureFin(heureFin)
                .semestre(semestre)
                .anneeAcademique(anneeAcademique)
                .build();
    }
}
