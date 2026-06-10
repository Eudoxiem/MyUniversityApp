package com.myuniversity.app.dto.admin;

import com.myuniversity.app.entity.Role;
import com.myuniversity.app.entity.User;
import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private Role role;
    private boolean actif;
    private boolean verrouille;
    private Instant dateVerrouillage;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .actif(user.getActif())
                .verrouille(user.getDateVerrouillage() != null)
                .dateVerrouillage(user.getDateVerrouillage())
                .build();
    }
}
