package com.myuniversity.app.service;

import com.myuniversity.app.entity.Etudiant;
import com.myuniversity.app.repository.EtudiantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtudiantServiceTest {

    @Mock
    private EtudiantRepository repository;

    @InjectMocks
    private EtudiantService service;

    @Test
    void save_shouldReturnSavedEtudiant() {
        Etudiant etudiant = Etudiant.builder()
                .matricule("ETU001").nom("Dupont").prenom("Jean").email("jean@test.com")
                .build();
        when(repository.save(etudiant)).thenReturn(etudiant);

        Etudiant result = service.save(etudiant);

        assertNotNull(result);
        assertEquals("ETU001", result.getMatricule());
        verify(repository).save(etudiant);
    }

    @Test
    void findById_whenExists_shouldReturnEtudiant() {
        Etudiant etudiant = Etudiant.builder().id(1L).matricule("ETU001").build();
        when(repository.findById(1L)).thenReturn(Optional.of(etudiant));

        Optional<Etudiant> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Etudiant> result = service.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByMatricule_shouldReturnEtudiant() {
        when(repository.findByMatricule("ETU001")).thenReturn(Optional.of(new Etudiant()));

        Optional<Etudiant> result = service.findByMatricule("ETU001");

        assertTrue(result.isPresent());
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void update_whenExists_shouldReturnUpdatedEtudiant() {
        Etudiant existing = Etudiant.builder().id(1L).matricule("ETU001").nom("Ancien").build();
        Etudiant updated = Etudiant.builder().id(1L).matricule("ETU001").nom("Nouveau").build();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(updated)).thenReturn(updated);

        Etudiant result = service.update(1L, updated);

        assertEquals("Nouveau", result.getNom());
    }

    @Test
    void update_whenNotExists_shouldThrow() {
        Etudiant etudiant = Etudiant.builder().id(99L).build();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update(99L, etudiant));
    }
}
