package com.myuniversity.app.service;

import com.myuniversity.app.dto.auth.AuthResponse;
import com.myuniversity.app.dto.auth.LoginRequest;
import com.myuniversity.app.dto.auth.RegisterRequest;
import com.myuniversity.app.entity.*;
import com.myuniversity.app.repository.*;
import com.myuniversity.app.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private TokenInvalideRepository tokenInvalideRepository;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private VerificationTokenRepository verificationTokenRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private EmailService emailService;
    @Mock private AuditService auditService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService,
                authenticationManager, tokenInvalideRepository, refreshTokenService,
                verificationTokenRepository, passwordResetTokenRepository, emailService, auditService);
        ReflectionTestUtils.setField(authService, "verificationExpiration", 86400000L);
    }

    @Test
    void register_whenEmailExists_shouldThrow() {
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);
        RegisterRequest request = RegisterRequest.builder().email("existing@test.com").build();
        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_whenValid_shouldCreateUserAndSendVerification() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Valid1@pass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));

        RegisterRequest request = RegisterRequest.builder()
                .email("new@test.com").password("Valid1@pass")
                .nom("Dupont").prenom("Jean").build();
        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("new@test.com", saved.getEmail());
        assertEquals(Role.ROLE_ETUDIANT, saved.getRole());
        assertFalse(saved.getActif());
        verify(verificationTokenRepository).save(any(VerificationToken.class));
        verify(emailService).envoyerEmail(eq("new@test.com"), anyString(), anyString());
    }

    @Test
    void login_whenInactif_shouldThrow() {
        User user = User.builder().email("test@test.com").actif(false).build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        LoginRequest request = LoginRequest.builder().email("test@test.com").password("pass").build();
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void login_whenLocked_shouldThrow() {
        User user = User.builder().email("test@test.com").actif(true)
                .dateVerrouillage(Instant.now().plusSeconds(300)).build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        LoginRequest request = LoginRequest.builder().email("test@test.com").password("pass").build();
        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void login_whenLockExpired_shouldClearLock() {
        User user = User.builder().email("test@test.com").actif(true).password("encoded")
                .dateVerrouillage(Instant.now().minusSeconds(60)).tentativesEchouees(3)
                .nom("Dupont").prenom("Jean").role(Role.ROLE_ETUDIANT).build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken("test@test.com", "pass"));
        when(jwtService.generateToken(anyString(), anyMap())).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(RefreshToken.builder().token("refresh-token").build());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        LoginRequest request = LoginRequest.builder().email("test@test.com").password("pass").build();
        AuthResponse response = authService.login(request);

        assertNull(user.getDateVerrouillage());
        assertEquals(0, user.getTentativesEchouees());
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_whenBadPassword_shouldIncrementAttemptsAndThrow() {
        User user = User.builder().email("test@test.com").actif(true).tentativesEchouees(0).build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Bad credentials"));

        LoginRequest request = LoginRequest.builder().email("test@test.com").password("wrong").build();
        assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals(1, user.getTentativesEchouees());
    }

    @Test
    void login_whenMaxAttemptsReached_shouldLockAccount() {
        User user = User.builder().email("test@test.com").actif(true).tentativesEchouees(4).build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Bad credentials"));

        LoginRequest request = LoginRequest.builder().email("test@test.com").password("wrong").build();
        assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals(5, user.getTentativesEchouees());
        assertNotNull(user.getDateVerrouillage());
    }

    @Test
    void login_whenValid_shouldReturnAuthResponse() {
        User user = User.builder().id(1L).email("test@test.com").actif(true).password("encoded")
                .nom("Dupont").prenom("Jean").role(Role.ROLE_ETUDIANT).build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken("test@test.com", "pass"));
        when(jwtService.generateToken(anyString(), anyMap())).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(RefreshToken.builder().token("refresh-token").build());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        LoginRequest request = LoginRequest.builder().email("test@test.com").password("pass").build();
        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void refresh_whenValid_shouldReturnNewTokens() {
        User user = User.builder().id(1L).email("test@test.com").nom("Dupont").prenom("Jean").role(Role.ROLE_ETUDIANT).build();
        RefreshToken stored = RefreshToken.builder().token("old-refresh").user(user)
                .dateExpiration(Instant.now().plusSeconds(3600)).revoque(false).build();
        when(refreshTokenService.findByToken("old-refresh")).thenReturn(Optional.of(stored));
        when(refreshTokenService.verifyExpiration(stored)).thenReturn(stored);
        when(jwtService.generateToken(anyString(), anyMap())).thenReturn("new-jwt");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(RefreshToken.builder().token("new-refresh").build());

        AuthResponse response = authService.refresh("old-refresh");

        assertEquals("new-jwt", response.getToken());
        assertEquals("new-refresh", response.getRefreshToken());
        verify(refreshTokenService).revokeAllUserTokens(1L);
    }

    @Test
    void refresh_whenTokenNotFound_shouldThrow() {
        when(refreshTokenService.findByToken("invalid")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.refresh("invalid"));
    }

    @Test
    void logout_shouldInvalidateToken() {
        when(jwtService.extractTokenId("token")).thenReturn("jti-123");
        when(jwtService.extractEmail("token")).thenReturn("test@test.com");
        when(jwtService.extractExpiration("token")).thenReturn(new Date());
        when(tokenInvalideRepository.findByJti("jti-123")).thenReturn(Optional.empty());
        when(tokenInvalideRepository.save(any(TokenInvalide.class))).thenAnswer(i -> i.getArgument(0));

        authService.logout("token");

        verify(tokenInvalideRepository).save(any(TokenInvalide.class));
        verify(auditService).log(eq("LOGOUT"), eq("test@test.com"), anyString());
    }

    @Test
    void verifyEmail_whenValid_shouldActivateUser() {
        User user = User.builder().email("test@test.com").actif(false).build();
        VerificationToken vt = VerificationToken.builder().token("valid-token").user(user)
                .dateExpiration(Instant.now().plusSeconds(3600)).utilise(false).build();
        when(verificationTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(vt));

        authService.verifyEmail("valid-token");

        assertTrue(vt.isUtilise());
        assertTrue(user.getActif());
    }

    @Test
    void verifyEmail_whenAlreadyUsed_shouldThrow() {
        VerificationToken vt = VerificationToken.builder().token("used-token")
                .dateExpiration(Instant.now().plusSeconds(3600)).utilise(true).build();
        when(verificationTokenRepository.findByToken("used-token")).thenReturn(Optional.of(vt));
        assertThrows(RuntimeException.class, () -> authService.verifyEmail("used-token"));
    }

    @Test
    void verifyEmail_whenExpired_shouldThrow() {
        VerificationToken vt = VerificationToken.builder().token("expired-token")
                .dateExpiration(Instant.now().minusSeconds(60)).utilise(false).build();
        when(verificationTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(vt));
        assertThrows(RuntimeException.class, () -> authService.verifyEmail("expired-token"));
    }

    @Test
    void forgotPassword_whenEmailExists_shouldSendEmail() {
        User user = User.builder().email("test@test.com").prenom("Jean").build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArgument(0));

        authService.forgotPassword("test@test.com");

        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).envoyerEmail(eq("test@test.com"), anyString(), anyString());
    }

    @Test
    void forgotPassword_whenEmailNotFound_shouldDoNothing() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        authService.forgotPassword("unknown@test.com");
        verify(passwordResetTokenRepository, never()).save(any());
    }

    @Test
    void resetPassword_whenValid_shouldUpdatePassword() {
        User user = User.builder().email("test@test.com").password("old")
                .tentativesEchouees(3).dateVerrouillage(Instant.now().minusSeconds(60)).build();
        PasswordResetToken prt = PasswordResetToken.builder().token("reset-token").user(user)
                .dateExpiration(Instant.now().plusSeconds(3600)).utilise(false).build();
        when(passwordResetTokenRepository.findByToken("reset-token")).thenReturn(Optional.of(prt));
        when(passwordEncoder.encode("NewPass1@")).thenReturn("encoded-new");

        authService.resetPassword("reset-token", "NewPass1@");

        assertTrue(prt.isUtilise());
        assertEquals("encoded-new", user.getPassword());
        assertEquals(0, user.getTentativesEchouees());
    }

    @Test
    void renvoyerVerification_whenInactif_shouldSendNewEmail() {
        User user = User.builder().email("test@test.com").actif(false).prenom("Jean").build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));

        authService.renvoyerVerification("test@test.com");

        verify(verificationTokenRepository).save(any(VerificationToken.class));
        verify(emailService).envoyerEmail(eq("test@test.com"), anyString(), anyString());
    }

    @Test
    void renvoyerVerification_whenAlreadyActive_shouldThrow() {
        User user = User.builder().email("test@test.com").actif(true).build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        assertThrows(RuntimeException.class, () -> authService.renvoyerVerification("test@test.com"));
    }
}
