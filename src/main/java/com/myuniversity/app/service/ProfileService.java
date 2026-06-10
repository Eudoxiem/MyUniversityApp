package com.myuniversity.app.service;

import com.myuniversity.app.dto.profile.ChangePasswordRequest;
import com.myuniversity.app.dto.profile.ProfileResponse;
import com.myuniversity.app.dto.profile.UpdateProfileRequest;
import com.myuniversity.app.entity.User;
import com.myuniversity.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public ProfileService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    public ProfileResponse getProfile() {
        User user = getCurrentUser();
        return toResponse(user);
    }

    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user = userRepository.save(user);
        log.info("Profil modifié - email: {}, nom: {} {}", user.getEmail(), user.getPrenom(), user.getNom());
        auditService.log("UPDATE_PROFILE", user.getEmail(), "Profil modifié");
        return toResponse(user);
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Tentative de changement de mot de passe avec mot de passe actuel incorrect - email: {}", user.getEmail());
            throw new RuntimeException("Mot de passe actuel incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Mot de passe modifié - email: {}", user.getEmail());
        auditService.log("CHANGE_PASSWORD", user.getEmail(), "Mot de passe modifié");
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof User user)) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        return userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    private ProfileResponse toResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .build();
    }
}
