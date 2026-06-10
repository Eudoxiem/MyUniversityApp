package com.myuniversity.app.service;

import com.myuniversity.app.dto.auth.AuthResponse;
import com.myuniversity.app.dto.auth.LoginRequest;
import com.myuniversity.app.dto.auth.RegisterRequest;
import com.myuniversity.app.entity.PasswordResetToken;
import com.myuniversity.app.entity.RefreshToken;
import com.myuniversity.app.entity.Role;
import com.myuniversity.app.entity.TokenInvalide;
import com.myuniversity.app.entity.User;
import com.myuniversity.app.entity.VerificationToken;
import com.myuniversity.app.repository.PasswordResetTokenRepository;
import com.myuniversity.app.repository.TokenInvalideRepository;
import com.myuniversity.app.repository.UserRepository;
import com.myuniversity.app.repository.VerificationTokenRepository;
import com.myuniversity.app.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class AuthService {

    @Value("${application.security.jwt.verification-expiration:86400000}")
    private long verificationExpiration;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenInvalideRepository tokenInvalideRepository;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       TokenInvalideRepository tokenInvalideRepository,
                       RefreshTokenService refreshTokenService,
                       VerificationTokenRepository verificationTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       EmailService emailService,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenInvalideRepository = tokenInvalideRepository;
        this.refreshTokenService = refreshTokenService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.auditService = auditService;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Tentative d'inscription avec un email existant: {}", request.getEmail());
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .role(Role.ROLE_ETUDIANT)
                .actif(false)
                .build();

        user = userRepository.save(user);
        log.info("Nouvel utilisateur inscrit (en attente de vérification) - email: {}, nom: {} {}", user.getEmail(), user.getPrenom(), user.getNom());
        auditService.log("REGISTER", user.getEmail(), "Inscription en attente de vérification email");

        VerificationToken verificationToken = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .dateExpiration(Instant.now().plusMillis(verificationExpiration))
                .build();
        verificationTokenRepository.save(verificationToken);

        String lien = "http://localhost:8443/api/auth/verify?token=" + verificationToken.getToken();
        emailService.envoyerEmail(
                user.getEmail(),
                "Vérification de votre email - MyUniversityApp",
                "Bonjour " + user.getPrenom() + ",\n\n"
                        + "Merci de vous être inscrit sur MyUniversityApp.\n\n"
                        + "Veuillez cliquer sur le lien ci-dessous pour vérifier votre adresse email :\n"
                        + lien + "\n\n"
                        + "Ce lien est valable 24 heures.\n\n"
                        + "Cordialement,\nL'équipe MyUniversityApp"
        );
        log.info("Email de vérification envoyé à: {}", user.getEmail());
    }

    private static final int MAX_TENTATIVES = 5;
    private static final int DUREE_VERROUILLAGE_MINUTES = 15;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user != null && !user.getActif()) {
            log.warn("Tentative de connexion sur compte non vérifié - email: {}", request.getEmail());
            throw new RuntimeException("Veuillez vérifier votre email avant de vous connecter");
        }

        if (user != null && user.getDateVerrouillage() != null) {
            if (Instant.now().isBefore(user.getDateVerrouillage())) {
                log.warn("Tentative sur compte verrouillé - email: {}", request.getEmail());
                throw new RuntimeException("Compte temporairement verrouillé. Réessayez dans 15 minutes.");
            }
            user.setDateVerrouillage(null);
            user.setTentativesEchouees(0);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            if (user != null) {
                user.setTentativesEchouees(user.getTentativesEchouees() + 1);
                if (user.getTentativesEchouees() >= MAX_TENTATIVES) {
                    user.setDateVerrouillage(Instant.now().plusSeconds(DUREE_VERROUILLAGE_MINUTES * 60));
                    log.warn("Compte verrouillé après {} tentatives - email: {}", MAX_TENTATIVES, request.getEmail());
                    auditService.log("VERROUILLAGE_COMPTE", request.getEmail(), "Compte verrouillé après " + MAX_TENTATIVES + " tentatives");
                }
                userRepository.save(user);
            }
            log.warn("Tentative de connexion échouée - email: {}", request.getEmail());
            auditService.log("LOGIN_ECHEC", request.getEmail(), "Tentative de connexion échouée");
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        if (user == null) {
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.warn("Utilisateur non trouvé après authentification - email: {}", request.getEmail());
                        return new RuntimeException("Email ou mot de passe incorrect");
                    });
        }

        user.setTentativesEchouees(0);
        user.setDateVerrouillage(null);
        userRepository.save(user);

        log.info("Connexion réussie - email: {}, rôle: {}", user.getEmail(), user.getRole());
        auditService.log("LOGIN", user.getEmail(), "Connexion réussie");

        String token = jwtService.generateToken(user.getEmail(),
                Map.of("role", user.getRole().name()));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return buildAuthResponse(user, token, refreshToken.getToken());
    }

    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken stored = refreshTokenService.findByToken(refreshTokenValue)
                .orElseThrow(() -> {
                    log.warn("Refresh token introuvable");
                    return new RuntimeException("Refresh token invalide");
                });

        refreshTokenService.verifyExpiration(stored);

        User user = stored.getUser();
        refreshTokenService.revokeAllUserTokens(user.getId());

        String newToken = jwtService.generateToken(user.getEmail(),
                Map.of("role", user.getRole().name()));
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        log.info("Token rafraîchi - email: {}", user.getEmail());
        return buildAuthResponse(user, newToken, newRefreshToken.getToken());
    }

    public void logout(String token) {
        String jti = jwtService.extractTokenId(token);
        String email = jwtService.extractEmail(token);
        Date expiration = jwtService.extractExpiration(token);

        userRepository.findByEmail(email).ifPresent(user ->
                refreshTokenService.revokeAllUserTokens(user.getId())
        );

        tokenInvalideRepository.findByJti(jti).orElseGet(() -> {
            auditService.log("LOGOUT", email, "Déconnexion");
            return tokenInvalideRepository.save(TokenInvalide.builder()
                    .jti(jti)
                    .dateExpiration(expiration.toInstant())
                    .dateInvalidation(Instant.now())
                    .build());
        });
        tokenInvalideRepository.deleteByDateExpirationBefore(Instant.now());
    }

    public void verifyEmail(String token) {
        VerificationToken vt = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Token de vérification invalide");
                    return new RuntimeException("Token de vérification invalide ou expiré");
                });

        if (vt.isUtilise()) {
            log.warn("Token de vérification déjà utilisé - email: {}", vt.getUser().getEmail());
            throw new RuntimeException("Token de vérification déjà utilisé");
        }

        if (vt.getDateExpiration().isBefore(Instant.now())) {
            log.warn("Token de vérification expiré - email: {}", vt.getUser().getEmail());
            throw new RuntimeException("Token de vérification expiré");
        }

        vt.setUtilise(true);
        verificationTokenRepository.save(vt);

        User user = vt.getUser();
        user.setActif(true);
        userRepository.save(user);

        log.info("Email vérifié avec succès - email: {}", user.getEmail());
        auditService.log("VERIFY_EMAIL", user.getEmail(), "Email vérifié avec succès");
    }

    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            PasswordResetToken prt = PasswordResetToken.builder()
                    .token(UUID.randomUUID().toString())
                    .user(user)
                    .dateExpiration(Instant.now().plusMillis(verificationExpiration))
                    .build();
            passwordResetTokenRepository.save(prt);

            String lien = "http://localhost:8443/reset-password?token=" + prt.getToken();
            emailService.envoyerEmail(
                    user.getEmail(),
                    "Réinitialisation de mot de passe - MyUniversityApp",
                    "Bonjour " + user.getPrenom() + ",\n\n"
                            + "Vous avez demandé la réinitialisation de votre mot de passe.\n\n"
                            + "Cliquez sur le lien ci-dessous pour définir un nouveau mot de passe :\n"
                            + lien + "\n\n"
                            + "Ce lien est valable 24 heures.\n\n"
                            + "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.\n\n"
                            + "Cordialement,\nL'équipe MyUniversityApp"
            );
            auditService.log("FORGOT_PASSWORD", email, "Email de réinitialisation envoyé");
            log.info("Email de réinitialisation envoyé à: {}", email);
        });
    }

    public void resetPassword(String token, String nouveauMotDePasse) {
        PasswordResetToken prt = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Token de réinitialisation invalide");
                    return new RuntimeException("Token de réinitialisation invalide ou expiré");
                });

        if (prt.isUtilise()) {
            log.warn("Token de réinitialisation déjà utilisé");
            throw new RuntimeException("Token de réinitialisation déjà utilisé");
        }

        if (prt.getDateExpiration().isBefore(Instant.now())) {
            log.warn("Token de réinitialisation expiré");
            throw new RuntimeException("Token de réinitialisation expiré");
        }

        prt.setUtilise(true);
        passwordResetTokenRepository.save(prt);

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(nouveauMotDePasse));
        user.setTentativesEchouees(0);
        user.setDateVerrouillage(null);
        userRepository.save(user);

        log.info("Mot de passe réinitialisé avec succès - email: {}", user.getEmail());
        auditService.log("RESET_PASSWORD", user.getEmail(), "Mot de passe réinitialisé avec succès");
    }

    public void renvoyerVerification(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("Demande de renvoi pour email inconnu: {}", email);
            return new RuntimeException("Email ou mot de passe incorrect");
        });

        if (user.getActif()) {
            log.warn("Demande de renvoi pour compte déjà actif - email: {}", email);
            throw new RuntimeException("Compte déjà vérifié");
        }

        VerificationToken vt = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .dateExpiration(Instant.now().plusMillis(verificationExpiration))
                .build();
        verificationTokenRepository.save(vt);

        String lien = "http://localhost:8443/api/auth/verify?token=" + vt.getToken();
        emailService.envoyerEmail(
                user.getEmail(),
                "Nouveau lien de vérification - MyUniversityApp",
                "Bonjour " + user.getPrenom() + ",\n\n"
                        + "Voici un nouveau lien pour vérifier votre adresse email :\n"
                        + lien + "\n\n"
                        + "Ce lien est valable 24 heures.\n\n"
                        + "Cordialement,\nL'équipe MyUniversityApp"
        );
        auditService.log("RESEND_VERIFICATION", email, "Nouvel email de vérification envoyé");
        log.info("Nouvel email de vérification envoyé à: {}", email);
    }

    private AuthResponse buildAuthResponse(User user, String token, String refreshToken) {
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .build();
    }
}
