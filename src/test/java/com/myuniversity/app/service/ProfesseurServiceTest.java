package com.myuniversity.app.service;

import com.myuniversity.app.entity.Professeur;
import com.myuniversity.app.repository.ProfesseurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfesseurServiceTest {

    @Mock
    private ProfesseurRepository repository;

    @InjectMocks
    private ProfesseurService service;

    @Test
    void save_shouldReturnSavedProfesseur() {
        Professeur professeur = Professeur.builder()
                .numeroEmploye("EMP001").nom("Martin").prenom("Sophie").email("sophie@test.com")
                .build();
        when(repository.save(professeur)).thenReturn(professeur);

        Professeur result = service.save(professeur);

        assertNotNull(result);
        assertEquals("EMP001", result.getNumeroEmploye());
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertFalse(service.findById(99L).isPresent());
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void update_whenNotExists_shouldThrow() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.update(99L, new Professeur()));
    }
}
