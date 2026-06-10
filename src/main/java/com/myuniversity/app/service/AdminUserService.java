package com.myuniversity.app.service;

import com.myuniversity.app.dto.admin.CreateUserRequest;
import com.myuniversity.app.dto.admin.UpdateUserRequest;
import com.myuniversity.app.dto.admin.UserResponse;
import com.myuniversity.app.entity.User;
import com.myuniversity.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public AdminUserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::fromEntity)
                .orElseThrow(() -> {
                    log.warn("Utilisateur introuvable - id: {}", id);
                    return new RuntimeException("Utilisateur introuvable");
                });
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Tentative de création avec un email existant: {}", request.getEmail());
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .role(request.getRole())
                .actif(true)
                .build();

        user = userRepository.save(user);
        log.info("Utilisateur créé par admin - id: {}, email: {}, rôle: {}", user.getId(), user.getEmail(), user.getRole());
        auditService.log("ADMIN_CREATE_USER", user.getEmail(), "Utilisateur créé avec rôle " + request.getRole());
        return UserResponse.fromEntity(user);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Utilisateur introuvable pour modification - id: {}", id);
                    return new RuntimeException("Utilisateur introuvable");
                });

        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setRole(request.getRole());
        user = userRepository.save(user);
        log.info("Utilisateur modifié par admin - id: {}, email: {}", id, user.getEmail());
        auditService.log("ADMIN_UPDATE_USER", user.getEmail(), "Utilisateur modifié");
        return UserResponse.fromEntity(user);
    }

    public void toggleActif(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Utilisateur introuvable pour activation/désactivation - id: {}", id);
                    return new RuntimeException("Utilisateur introuvable");
                });

        user.setActif(!user.getActif());
        userRepository.save(user);
        log.info("Compte {} par admin - id: {}, email: {}",
                user.getActif() ? "activé" : "désactivé", id, user.getEmail());
        auditService.log("ADMIN_TOGGLE_ACTIF", user.getEmail(), "Compte " + (user.getActif() ? "activé" : "désactivé"));
    }

    public void debloquer(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Utilisateur introuvable pour déblocage - id: {}", id);
                    return new RuntimeException("Utilisateur introuvable");
                });

        user.setTentativesEchouees(0);
        user.setDateVerrouillage(null);
        userRepository.save(user);
        log.info("Compte débloqué par admin - id: {}, email: {}", id, user.getEmail());
        auditService.log("ADMIN_DEBLOQUER", user.getEmail(), "Compte débloqué");
    }
}
