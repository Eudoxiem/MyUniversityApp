package com.myuniversity.app.dto.presence;

import com.myuniversity.app.entity.Presence;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PresenceDTO {
    private Long id;
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantMatricule;
    private Long coursId;
    private String coursNom;
    private String coursCode;
    private LocalDate date;
    private boolean present;
    private boolean justifie;
    private String justification;

    public static PresenceDTO fromEntity(Presence presence) {
        return PresenceDTO.builder()
                .id(presence.getId())
                .etudiantId(presence.getEtudiant().getId())
                .etudiantNom(presence.getEtudiant().getNom())
                .etudiantPrenom(presence.getEtudiant().getPrenom())
                .etudiantMatricule(presence.getEtudiant().getMatricule())
                .coursId(presence.getCours().getId())
                .coursNom(presence.getCours().getNom())
                .coursCode(presence.getCours().getCode())
                .date(presence.getDate())
                .present(presence.isPresent())
                .justifie(presence.isJustifie())
                .justification(presence.getJustification())
                .build();
    }
}
