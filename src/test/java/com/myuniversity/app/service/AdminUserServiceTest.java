package com.myuniversity.app.service;

import com.myuniversity.app.dto.admin.CreateUserRequest;
import com.myuniversity.app.dto.admin.UpdateUserRequest;
import com.myuniversity.app.dto.admin.UserResponse;
import com.myuniversity.app.entity.Role;
import com.myuniversity.app.entity.User;
import com.myuniversity.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuditService auditService;

    private AdminUserService service;

    @BeforeEach
    void setUp() {
        service = new AdminUserService(userRepository, passwordEncoder, auditService);
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(
                User.builder().id(1L).email("a@test.com").role(Role.ROLE_ETUDIANT).build(),
                User.builder().id(2L).email("b@test.com").role(Role.ROLE_ADMIN).build()
        ));
        assertEquals(2, service.getAllUsers().size());
    }

    @Test
    void getUserById_whenExists_shouldReturn() {
        User user = User.builder().id(1L).email("test@test.com").nom("Dupont").prenom("Jean").role(Role.ROLE_ETUDIANT).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserResponse result = service.getUserById(1L);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void getUserById_whenNotExists_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getUserById(99L));
    }

    @Test
    void createUser_whenEmailExists_shouldThrow() {
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);
        CreateUserRequest request = CreateUserRequest.builder().email("existing@test.com").build();
        assertThrows(RuntimeException.class, () -> service.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_whenValid_shouldReturnSaved() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Pass1@")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> { User u = i.getArgument(0); u.setId(1L); return u; });

        CreateUserRequest request = CreateUserRequest.builder()
                .email("new@test.com").password("Pass1@").nom("Dupont").prenom("Jean").role(Role.ROLE_PROFESSEUR).build();
        UserResponse result = service.createUser(request);

        assertEquals("new@test.com", result.getEmail());
        assertTrue(result.isActif());
        assertEquals(Role.ROLE_PROFESSEUR, result.getRole());
    }

    @Test
    void updateUser_shouldReturnUpdated() {
        User existing = User.builder().id(1L).email("test@test.com").nom("Old").prenom("Old").role(Role.ROLE_ETUDIANT).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        UpdateUserRequest request = UpdateUserRequest.builder().nom("NewNom").prenom("NewPrenom").role(Role.ROLE_PROFESSEUR).build();
        UserResponse result = service.updateUser(1L, request);

        assertEquals("NewNom", result.getNom());
    }

    @Test
    void toggleActif_shouldFlipStatus() {
        User user = User.builder().id(1L).actif(false).email("test@test.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        service.toggleActif(1L);
        assertTrue(user.getActif());
    }

    @Test
    void debloquer_shouldClearLockout() {
        User user = User.builder().id(1L).email("test@test.com").tentativesEchouees(5)
                .dateVerrouillage(Instant.now().plusSeconds(300)).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        service.debloquer(1L);
        assertEquals(0, user.getTentativesEchouees());
        assertNull(user.getDateVerrouillage());
    }
}
