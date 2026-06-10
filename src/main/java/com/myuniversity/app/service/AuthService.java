package com.myuniversity.app.service;

import com.myuniversity.app.dto.auth.AuthResponse;
import com.myuniversity.app.dto.auth.LoginRequest;
import com.myuniversity.app.dto.auth.RegisterRequest;
import com.myuniversity.app.entity.Role;
import com.myuniversity.app.entity.TokenInvalide;
import com.myuniversity.app.entity.User;
import com.myuniversity.app.repository.TokenInvalideRepository;
import com.myuniversity.app.repository.UserRepository;
import com.myuniversity.app.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenInvalideRepository tokenInvalideRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       TokenInvalideRepository tokenInvalideRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenInvalideRepository = tokenInvalideRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Tentative d'inscription avec un email existant: {}", request.getEmail());
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .role(Role.ROLE_ETUDIANT)
                .build();

        user = userRepository.save(user);
        log.info("Nouvel utilisateur inscrit - email: {}, nom: {} {}, rôle: {}", user.getEmail(), user.getPrenom(), user.getNom(), user.getRole());

        String token = jwtService.generateToken(user.getEmail(),
                Map.of("role", user.getRole().name()));

        return buildAuthResponse(user, token);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            log.warn("Tentative de connexion échouée - email: {}", request.getEmail());
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé après authentification - email: {}", request.getEmail());
                    return new RuntimeException("Email ou mot de passe incorrect");
                });

        log.info("Connexion réussie - email: {}, rôle: {}", user.getEmail(), user.getRole());

        String token = jwtService.generateToken(user.getEmail(),
                Map.of("role", user.getRole().name()));

        return buildAuthResponse(user, token);
    }

    public void logout(String token) {
        String jti = jwtService.extractTokenId(token);
        String email = jwtService.extractEmail(token);
        Date expiration = jwtService.extractExpiration(token);
        tokenInvalideRepository.findByJti(jti).orElseGet(() -> {
            log.info("Déconnexion - email: {}, jti: {}", email, jti);
            return tokenInvalideRepository.save(TokenInvalide.builder()
                    .jti(jti)
                    .dateExpiration(expiration.toInstant())
                    .dateInvalidation(Instant.now())
                    .build());
        });
        tokenInvalideRepository.deleteByDateExpirationBefore(Instant.now());
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .build();
    }
}
