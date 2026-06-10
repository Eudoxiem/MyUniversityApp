package com.myuniversity.app.dto.presence;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AppelRequest {

    @NotNull(message = "Le cours est obligatoire")
    private Long coursId;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "La liste des présences est obligatoire")
    private List<PresenceIndividuelle> presences;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class PresenceIndividuelle {
        @NotNull
        private Long etudiantId;

        private boolean present;
    }
}
