package com.myuniversity.app.dto.profile;

import com.myuniversity.app.entity.Role;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProfileResponse {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private Role role;
}
