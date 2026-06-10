package com.myuniversity.app.dto.auth;

import com.myuniversity.app.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ResetPasswordRequest {

    @NotBlank(message = "Le token est obligatoire")
    private String token;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @ValidPassword
    private String nouveauMotDePasse;
}
