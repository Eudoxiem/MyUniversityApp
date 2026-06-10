package com.myuniversity.app.dto.presence;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PresenceRequest {

    @NotNull(message = "L'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "Le cours est obligatoire")
    private Long coursId;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    private boolean present;

    private boolean justifie;

    private String justification;
}
