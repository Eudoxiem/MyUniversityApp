package com.myuniversity.app.service;

import com.myuniversity.app.dto.presence.AppelRequest;
import com.myuniversity.app.dto.presence.PresenceDTO;
import com.myuniversity.app.dto.presence.PresenceRequest;
import com.myuniversity.app.entity.Cours;
import com.myuniversity.app.entity.Etudiant;
import com.myuniversity.app.entity.Presence;
import com.myuniversity.app.repository.CoursRepository;
import com.myuniversity.app.repository.EtudiantRepository;
import com.myuniversity.app.repository.PresenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresenceServiceTest {

    @Mock
    private PresenceRepository presenceRepository;
    @Mock
    private EtudiantRepository etudiantRepository;
    @Mock
    private CoursRepository coursRepository;

    @InjectMocks
    private PresenceService service;

    private final Etudiant etudiant = Etudiant.builder().id(1L).nom("Dupont").prenom("Jean").matricule("ETU001").build();
    private final Cours cours = Cours.builder().id(1L).nom("Math").code("MATH101").build();

    @Test
    void getPresencesByCoursAndDate_shouldReturnList() {
        when(presenceRepository.findByCoursIdAndDateOrderByEtudiantNom(1L, LocalDate.now()))
                .thenReturn(List.of(Presence.builder().id(1L).etudiant(etudiant).cours(cours).build()));

        List<PresenceDTO> result = service.getPresencesByCoursAndDate(1L, LocalDate.now());

        assertEquals(1, result.size());
    }

    @Test
    void getPresencesByEtudiant_shouldReturnList() {
        when(presenceRepository.findByEtudiantIdOrderByDateDesc(1L))
                .thenReturn(List.of(Presence.builder().id(1L).etudiant(etudiant).cours(cours).build()));

        List<PresenceDTO> result = service.getPresencesByEtudiant(1L);

        assertEquals(1, result.size());
    }

    @Test
    void createPresence_whenNoDuplicate_shouldReturnSaved() {
        PresenceRequest request = PresenceRequest.builder()
                .etudiantId(1L).coursId(1L).date(LocalDate.now())
                .present(true).build();
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(coursRepository.findById(1L)).thenReturn(Optional.of(cours));
        when(presenceRepository.findByEtudiantIdAndCoursIdAndDate(1L, 1L, LocalDate.now()))
                .thenReturn(Optional.empty());
        Presence saved = Presence.builder().id(1L).etudiant(etudiant).cours(cours)
                .date(LocalDate.now()).present(true).build();
        when(presenceRepository.save(any(Presence.class))).thenReturn(saved);

        PresenceDTO result = service.createPresence(request);

        assertNotNull(result);
        verify(presenceRepository).save(any(Presence.class));
    }

    @Test
    void createPresence_whenDuplicate_shouldThrow() {
        PresenceRequest request = PresenceRequest.builder()
                .etudiantId(1L).coursId(1L).date(LocalDate.now()).build();
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(coursRepository.findById(1L)).thenReturn(Optional.of(cours));
        when(presenceRepository.findByEtudiantIdAndCoursIdAndDate(1L, 1L, LocalDate.now()))
                .thenReturn(Optional.of(new Presence()));

        assertThrows(RuntimeException.class, () -> service.createPresence(request));
    }

    @Test
    void createPresence_whenEtudiantNotFound_shouldThrow() {
        when(etudiantRepository.findById(99L)).thenReturn(Optional.empty());

        PresenceRequest request = PresenceRequest.builder().etudiantId(99L).build();

        assertThrows(RuntimeException.class, () -> service.createPresence(request));
    }

    @Test
    void faireAppel_whenNew_shouldCreatePresences() {
        AppelRequest request = AppelRequest.builder()
                .coursId(1L).date(LocalDate.now())
                .presences(List.of(
                        new AppelRequest.PresenceIndividuelle(1L, true),
                        new AppelRequest.PresenceIndividuelle(2L, false)
                ))
                .build();
        Etudiant etudiant2 = Etudiant.builder().id(2L).nom("Martin").prenom("Sophie").matricule("ETU002").build();
        when(coursRepository.findById(1L)).thenReturn(Optional.of(cours));
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(etudiantRepository.findById(2L)).thenReturn(Optional.of(etudiant2));
        when(presenceRepository.findByEtudiantIdAndCoursIdAndDate(1L, 1L, LocalDate.now()))
                .thenReturn(Optional.empty());
        when(presenceRepository.findByEtudiantIdAndCoursIdAndDate(2L, 1L, LocalDate.now()))
                .thenReturn(Optional.empty());
        when(presenceRepository.save(any(Presence.class)))
                .thenAnswer(i -> i.getArgument(0));

        List<PresenceDTO> result = service.faireAppel(request);

        assertEquals(2, result.size());
        verify(presenceRepository, times(2)).save(any(Presence.class));
    }

    @Test
    void faireAppel_whenExisting_shouldUpdate() {
        AppelRequest request = AppelRequest.builder()
                .coursId(1L).date(LocalDate.now())
                .presences(List.of(new AppelRequest.PresenceIndividuelle(1L, true)))
                .build();
        Presence existing = Presence.builder().id(1L).etudiant(etudiant).cours(cours)
                .date(LocalDate.now()).present(false).justifie(true)
                .justification("Malade").build();
        when(coursRepository.findById(1L)).thenReturn(Optional.of(cours));
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(presenceRepository.findByEtudiantIdAndCoursIdAndDate(1L, 1L, LocalDate.now()))
                .thenReturn(Optional.of(existing));
        when(presenceRepository.save(any(Presence.class)))
                .thenAnswer(i -> i.getArgument(0));

        List<PresenceDTO> result = service.faireAppel(request);

        assertTrue(result.get(0).isPresent());
        assertFalse(result.get(0).isJustifie());
        assertNull(result.get(0).getJustification());
    }

    @Test
    void updatePresence_shouldReturnUpdated() {
        PresenceRequest request = PresenceRequest.builder()
                .present(true).justifie(false).build();
        Presence existing = Presence.builder().id(1L).etudiant(etudiant).cours(cours)
                .present(false).build();
        when(presenceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(presenceRepository.save(any(Presence.class))).thenAnswer(i -> i.getArgument(0));

        PresenceDTO result = service.updatePresence(1L, request);

        assertTrue(result.isPresent());
    }

    @Test
    void deletePresence_shouldCallRepository() {
        service.deletePresence(1L);
        verify(presenceRepository).deleteById(1L);
    }
}
