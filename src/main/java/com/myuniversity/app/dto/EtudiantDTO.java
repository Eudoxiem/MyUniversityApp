package com.myuniversity.app.dto;

import com.myuniversity.app.entity.Etudiant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EtudiantDTO {

    private Long id;

    @NotBlank(message = "Le matricule est obligatoire")
    private String matricule;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    private String telephone;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    private String adresse;

    public static EtudiantDTO fromEntity(Etudiant etudiant) {
        return EtudiantDTO.builder()
                .id(etudiant.getId())
                .matricule(etudiant.getMatricule())
                .nom(etudiant.getNom())
                .prenom(etudiant.getPrenom())
                .email(etudiant.getEmail())
                .telephone(etudiant.getTelephone())
                .dateNaissance(etudiant.getDateNaissance())
                .adresse(etudiant.getAdresse())
                .build();
    }

    public Etudiant toEntity() {
        return Etudiant.builder()
                .id(id)
                .matricule(matricule)
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .telephone(telephone)
                .dateNaissance(dateNaissance)
                .adresse(adresse)
                .build();
    }
}
