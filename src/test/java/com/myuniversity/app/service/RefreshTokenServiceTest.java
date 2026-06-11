package com.myuniversity.app.service;

import com.myuniversity.app.entity.RefreshToken;
import com.myuniversity.app.entity.User;
import com.myuniversity.app.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService service;

    private final User user = User.builder().id(1L).email("test@test.com").build();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "refreshExpiration", 604800000L);
    }

    @Test
    void createRefreshToken_shouldReturnSavedToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        RefreshToken result = service.createRefreshToken(user);

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertFalse(result.isRevoque());
        assertEquals(user, result.getUser());
        verify(refreshTokenRepository).deleteByUser_Id(1L);
    }

    @Test
    void findByToken_whenExists_shouldReturn() {
        RefreshToken rt = RefreshToken.builder().token("abc").build();
        when(refreshTokenRepository.findByToken("abc")).thenReturn(Optional.of(rt));

        Optional<RefreshToken> result = service.findByToken("abc");

        assertTrue(result.isPresent());
        assertEquals("abc", result.get().getToken());
    }

    @Test
    void findByToken_whenNotExists_shouldReturnEmpty() {
        when(refreshTokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

        Optional<RefreshToken> result = service.findByToken("unknown");

        assertFalse(result.isPresent());
    }

    @Test
    void verifyExpiration_whenValid_shouldReturnToken() {
        RefreshToken rt = RefreshToken.builder()
                .dateExpiration(Instant.now().plusSeconds(3600))
                .revoque(false)
                .build();

        RefreshToken result = service.verifyExpiration(rt);

        assertNotNull(result);
    }

    @Test
    void verifyExpiration_whenRevoked_shouldThrow() {
        RefreshToken rt = RefreshToken.builder()
                .dateExpiration(Instant.now().plusSeconds(3600))
                .revoque(true)
                .build();

        assertThrows(RuntimeException.class, () -> service.verifyExpiration(rt));
    }

    @Test
    void verifyExpiration_whenExpired_shouldThrowAndDelete() {
        RefreshToken rt = RefreshToken.builder()
                .id(1L)
                .dateExpiration(Instant.now().minusSeconds(60))
                .revoque(false)
                .build();

        assertThrows(RuntimeException.class, () -> service.verifyExpiration(rt));
        verify(refreshTokenRepository).delete(rt);
    }

    @Test
    void revokeAllUserTokens_shouldCallRepository() {
        service.revokeAllUserTokens(1L);
        verify(refreshTokenRepository).deleteByUser_Id(1L);
    }

    @Test
    void cleanupExpiredTokens_shouldCallRepository() {
        service.cleanupExpiredTokens();
        verify(refreshTokenRepository).deleteByDateExpirationBefore(any(Instant.class));
    }
}
