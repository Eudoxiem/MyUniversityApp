package com.myuniversity.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EmailRequestDTO {

    @NotBlank(message = "Le destinataire est obligatoire")
    @Email(message = "L'email doit être valide")
    private String destinataire;

    @NotBlank(message = "Le sujet est obligatoire")
    private String sujet;

    @NotBlank(message = "Le corps du message est obligatoire")
    private String corps;
}
