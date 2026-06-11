package com.myuniversity.app.service;

import com.myuniversity.app.dto.profile.ChangePasswordRequest;
import com.myuniversity.app.dto.profile.ProfileResponse;
import com.myuniversity.app.dto.profile.UpdateProfileRequest;
import com.myuniversity.app.entity.Role;
import com.myuniversity.app.entity.User;
import com.myuniversity.app.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuditService auditService;

    private ProfileService service;

    @BeforeEach
    void setUp() {
        service = new ProfileService(userRepository, passwordEncoder, auditService);
        User user = User.builder()
                .id(1L).email("test@test.com").nom("Dupont").prenom("Jean")
                .password("encoded").role(Role.ROLE_ETUDIANT).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_ETUDIANT")))
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getProfile_shouldReturnCurrentUser() {
        User user = User.builder().id(1L).email("test@test.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ProfileResponse result = service.getProfile();
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void updateProfile_shouldReturnUpdated() {
        User user = User.builder().id(1L).email("test@test.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UpdateProfileRequest request = UpdateProfileRequest.builder().nom("NewNom").prenom("NewPrenom").build();
        ProfileResponse result = service.updateProfile(request);

        assertEquals("NewNom", result.getNom());
        verify(auditService).log(eq("UPDATE_PROFILE"), anyString(), anyString());
    }

    @Test
    void changePassword_whenCurrentPasswordCorrect_shouldUpdate() {
        User user = User.builder().id(1L).email("test@test.com")
                .password("encoded").role(Role.ROLE_ETUDIANT).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encoded")).thenReturn(true);
        when(passwordEncoder.encode("newPass1@")).thenReturn("new-encoded");

        ChangePasswordRequest request = ChangePasswordRequest.builder().currentPassword("oldPass").newPassword("newPass1@").build();
        service.changePassword(request);

        assertEquals("new-encoded", user.getPassword());
    }

    @Test
    void changePassword_whenCurrentPasswordWrong_shouldThrow() {
        User user = User.builder().id(1L).email("test@test.com")
                .password("encoded").role(Role.ROLE_ETUDIANT).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        ChangePasswordRequest request = ChangePasswordRequest.builder().currentPassword("wrong").newPassword("newPass1@").build();
        assertThrows(RuntimeException.class, () -> service.changePassword(request));
    }
}
