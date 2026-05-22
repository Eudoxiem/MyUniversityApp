package com.myuniversity.app.dto;

import com.myuniversity.app.entity.Professeur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProfesseurDTO {

    private Long id;

    @NotBlank(message = "Le numéro d'employé est obligatoire")
    private String numeroEmploye;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    private String telephone;

    private String specialite;

    public static ProfesseurDTO fromEntity(Professeur professeur) {
        return ProfesseurDTO.builder()
                .id(professeur.getId())
                .numeroEmploye(professeur.getNumeroEmploye())
                .nom(professeur.getNom())
                .prenom(professeur.getPrenom())
                .email(professeur.getEmail())
                .telephone(professeur.getTelephone())
                .specialite(professeur.getSpecialite())
                .build();
    }

    public Professeur toEntity() {
        return Professeur.builder()
                .id(id)
                .numeroEmploye(numeroEmploye)
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .telephone(telephone)
                .specialite(specialite)
                .build();
    }
}
