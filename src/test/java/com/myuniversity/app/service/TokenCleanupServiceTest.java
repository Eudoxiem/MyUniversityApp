package com.myuniversity.app.service;

import com.myuniversity.app.repository.PasswordResetTokenRepository;
import com.myuniversity.app.repository.RefreshTokenRepository;
import com.myuniversity.app.repository.TokenInvalideRepository;
import com.myuniversity.app.repository.VerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenCleanupServiceTest {

    @Mock
    private TokenInvalideRepository tokenInvalideRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private TokenCleanupService service;

    @Test
    void nettoyerTokensExpires_shouldDeleteFromAllRepositories() {
        service.nettoyerTokensExpires();

        verify(tokenInvalideRepository).deleteByDateExpirationBefore(any(Instant.class));
        verify(refreshTokenRepository).deleteByDateExpirationBefore(any(Instant.class));
        verify(verificationTokenRepository).deleteByDateExpirationBefore(any(Instant.class));
        verify(passwordResetTokenRepository).deleteByDateExpirationBefore(any(Instant.class));
    }
}
