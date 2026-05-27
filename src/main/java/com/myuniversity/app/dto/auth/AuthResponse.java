package com.myuniversity.app.dto.auth;

import com.myuniversity.app.entity.Role;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private Long userId;
    private String email;
    private String nom;
    private String prenom;
    private Role role;
}
