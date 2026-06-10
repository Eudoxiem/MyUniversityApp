package com.myuniversity.app.service;

import com.myuniversity.app.repository.PasswordResetTokenRepository;
import com.myuniversity.app.repository.RefreshTokenRepository;
import com.myuniversity.app.repository.TokenInvalideRepository;
import com.myuniversity.app.repository.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@Transactional
public class TokenCleanupService {

    private final TokenInvalideRepository tokenInvalideRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public TokenCleanupService(TokenInvalideRepository tokenInvalideRepository,
                               RefreshTokenRepository refreshTokenRepository,
                               VerificationTokenRepository verificationTokenRepository,
                               PasswordResetTokenRepository passwordResetTokenRepository) {
        this.tokenInvalideRepository = tokenInvalideRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Scheduled(fixedRate = 3600000)
    public void nettoyerTokensExpires() {
        log.debug("Nettoyage des tokens expirés...");
        tokenInvalideRepository.deleteByDateExpirationBefore(Instant.now());
        refreshTokenRepository.deleteByDateExpirationBefore(Instant.now());
        verificationTokenRepository.deleteByDateExpirationBefore(Instant.now());
        passwordResetTokenRepository.deleteByDateExpirationBefore(Instant.now());
        log.debug("Nettoyage des tokens expirés terminé");
    }
}
