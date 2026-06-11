package com.myuniversity.app.service;

import com.myuniversity.app.dto.stats.StatsCoursDTO;
import com.myuniversity.app.dto.stats.StatsGeneralesDTO;
import com.myuniversity.app.entity.*;
import com.myuniversity.app.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;
    @Mock
    private ProfesseurRepository professeurRepository;
    @Mock
    private CoursRepository coursRepository;
    @Mock
    private SalleRepository salleRepository;
    @Mock
    private InscriptionRepository inscriptionRepository;
    @Mock
    private PaiementRepository paiementRepository;
    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private StatsService service;

    @Test
    void getStatsGenerales_shouldReturnAggregatedStats() {
        when(etudiantRepository.count()).thenReturn(100L);
        when(professeurRepository.count()).thenReturn(20L);
        when(coursRepository.count()).thenReturn(15L);
        when(salleRepository.count()).thenReturn(10L);
        when(inscriptionRepository.count()).thenReturn(200L);
        when(paiementRepository.count()).thenReturn(150L);
        when(paiementRepository.findByStatut(StatutPaiement.EN_ATTENTE)).thenReturn(List.of(new Paiement(), new Paiement()));
        when(paiementRepository.findByStatut(StatutPaiement.PAYE)).thenReturn(List.of(new Paiement()));
        when(paiementRepository.findByStatut(StatutPaiement.EN_RETARD)).thenReturn(List.of());

        StatsGeneralesDTO result = service.getStatsGenerales();

        assertEquals(100, result.getTotalEtudiants());
        assertEquals(20, result.getTotalProfesseurs());
        assertEquals(15, result.getTotalCours());
        assertEquals(10, result.getTotalSalles());
        assertEquals(200, result.getTotalInscriptions());
        assertEquals(150, result.getTotalPaiements());
        assertEquals(2, result.getTotalPaiementsEnAttente());
        assertEquals(1, result.getTotalPaiementsPayes());
        assertEquals(0, result.getTotalPaiementsEnRetard());
    }

    @Test
    void getStatsCours_shouldReturnPerCourseStats() {
        Cours cours1 = Cours.builder().id(1L).nom("Maths").code("MATH101").build();
        Inscription ins1 = Inscription.builder().id(1L).cours(cours1).build();
        Note note1 = Note.builder().id(1L).valeur(15.0).inscription(ins1).build();
        Note note2 = Note.builder().id(1L).valeur(10.0).inscription(ins1).build();

        when(coursRepository.findAll()).thenReturn(List.of(cours1));
        when(inscriptionRepository.findByCoursId(1L)).thenReturn(List.of(ins1));
        when(noteRepository.findByInscription_Cours_Id(1L)).thenReturn(List.of(note1, note2));

        List<StatsCoursDTO> result = service.getStatsCours();

        assertEquals(1, result.size());
        StatsCoursDTO dto = result.get(0);
        assertEquals(1L, dto.getCoursId());
        assertEquals("Maths", dto.getCoursNom());
        assertEquals("MATH101", dto.getCode());
        assertEquals(1, dto.getNombreEtudiants());
        assertEquals(12.5, dto.getMoyenneGenerale());
    }

    @Test
    void getStatsCours_whenNoNotes_shouldReturnZeroAverage() {
        Cours cours1 = Cours.builder().id(1L).nom("Physique").code("PHY101").build();

        when(coursRepository.findAll()).thenReturn(List.of(cours1));
        when(inscriptionRepository.findByCoursId(1L)).thenReturn(List.of());
        when(noteRepository.findByInscription_Cours_Id(1L)).thenReturn(List.of());

        List<StatsCoursDTO> result = service.getStatsCours();

        assertEquals(1, result.size());
        assertEquals(0.0, result.get(0).getMoyenneGenerale());
        assertEquals(0, result.get(0).getNombreEtudiants());
    }
}
