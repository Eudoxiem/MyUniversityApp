package com.myuniversity.app.service;

import com.myuniversity.app.entity.*;
import com.myuniversity.app.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmploiDuTempsServiceTest {

    @Mock
    private EmploiDuTempsRepository edtRepository;
    @Mock
    private CoursRepository coursRepository;
    @Mock
    private SalleRepository salleRepository;
    @Mock
    private ProfesseurRepository professeurRepository;

    @InjectMocks
    private EmploiDuTempsService service;

    @Test
    void findAll_shouldReturnAll() {
        when(edtRepository.findAll()).thenReturn(List.of(new EmploiDuTemps()));

        List<EmploiDuTemps> result = service.findAll();

        assertEquals(1, result.size());
        verify(edtRepository).findAll();
    }

    @Test
    void findById_whenExists_shouldReturn() {
        EmploiDuTemps edt = EmploiDuTemps.builder().id(1L).build();
        when(edtRepository.findById(1L)).thenReturn(Optional.of(edt));

        EmploiDuTemps result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_whenNotExists_shouldThrow() {
        when(edtRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(99L));
    }

    @Test
    void save_whenNoConflict_shouldReturnSaved() {
        Cours cours = Cours.builder().id(1L).build();
        Salle salle = Salle.builder().id(1L).build();
        Professeur professeur = Professeur.builder().id(1L).build();
        EmploiDuTemps edt = EmploiDuTemps.builder()
                .jourSemaine(JourSemaine.LUNDI)
                .heureDebut(LocalTime.of(8, 0))
                .heureFin(LocalTime.of(10, 0))
                .semestre("S1")
                .anneeAcademique("2025-2026")
                .build();

        when(coursRepository.findById(1L)).thenReturn(Optional.of(cours));
        when(salleRepository.findById(1L)).thenReturn(Optional.of(salle));
        when(professeurRepository.findById(1L)).thenReturn(Optional.of(professeur));
        when(edtRepository.findConflitsSalle(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());
        when(edtRepository.findConflitsProfesseur(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());
        when(edtRepository.save(edt)).thenReturn(edt);

        EmploiDuTemps result = service.save(edt, 1L, 1L, 1L);

        assertNotNull(result);
        verify(edtRepository).save(edt);
    }

    @Test
    void save_whenSalleConflict_shouldThrow() {
        when(coursRepository.findById(1L)).thenReturn(Optional.of(Cours.builder().id(1L).build()));
        when(salleRepository.findById(1L)).thenReturn(Optional.of(Salle.builder().id(1L).build()));
        when(professeurRepository.findById(1L)).thenReturn(Optional.of(Professeur.builder().id(1L).build()));
        when(edtRepository.findConflitsSalle(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(new EmploiDuTemps()));

        EmploiDuTemps edt = EmploiDuTemps.builder()
                .jourSemaine(JourSemaine.LUNDI)
                .heureDebut(LocalTime.of(8, 0))
                .heureFin(LocalTime.of(10, 0))
                .semestre("S1")
                .anneeAcademique("2025-2026")
                .build();

        assertThrows(RuntimeException.class, () -> service.save(edt, 1L, 1L, 1L));
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete(1L);
        verify(edtRepository).deleteById(1L);
    }

    @Test
    void update_whenExists_shouldReturnUpdated() {
        EmploiDuTemps existing = EmploiDuTemps.builder().id(1L).build();
        EmploiDuTemps updated = EmploiDuTemps.builder().id(1L).semestre("S2").build();
        when(edtRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(edtRepository.save(updated)).thenReturn(updated);

        EmploiDuTemps result = service.update(1L, updated);

        assertEquals("S2", result.getSemestre());
    }
}
