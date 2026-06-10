package com.myuniversity.app.service;

import com.myuniversity.app.entity.RefreshToken;
import com.myuniversity.app.entity.User;
import com.myuniversity.app.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class RefreshTokenService {

    @Value("${application.security.jwt.refresh-expiration}")
    private long refreshExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user) {
        revokeAllUserTokens(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .dateExpiration(Instant.now().plusMillis(refreshExpiration))
                .revoque(false)
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        log.debug("Refresh token créé - userId: {}", user.getId());
        return saved;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.isRevoque()) {
            log.warn("Refresh token révoqué - id: {}", refreshToken.getId());
            throw new RuntimeException("Refresh token révoqué");
        }
        if (refreshToken.getDateExpiration().isBefore(Instant.now())) {
            log.warn("Refresh token expiré - id: {}", refreshToken.getId());
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expiré");
        }
        return refreshToken;
    }

    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.deleteByUser_Id(userId);
    }

    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteByDateExpirationBefore(Instant.now());
    }
}
