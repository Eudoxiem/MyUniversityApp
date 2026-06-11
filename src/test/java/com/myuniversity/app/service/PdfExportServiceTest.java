package com.myuniversity.app.service;

import com.myuniversity.app.entity.*;
import com.myuniversity.app.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfExportServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private InscriptionRepository inscriptionRepository;
    @Mock
    private CoursRepository coursRepository;
    @Mock
    private PaiementRepository paiementRepository;

    @InjectMocks
    private PdfExportService service;

    @Test
    void exportBulletin_whenEtudiantNotFound_shouldThrow() {
        when(etudiantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.exportBulletin(99L));
    }

    @Test
    void exportBulletin_whenValid_shouldReturnPdf() {
        Etudiant etudiant = Etudiant.builder().id(1L).nom("Dupont").prenom("Jean").matricule("MAT001").build();
        Cours cours = Cours.builder().id(1L).nom("Maths").build();
        Inscription inscription = Inscription.builder().id(1L).etudiant(etudiant).cours(cours).build();
        Note note = Note.builder().id(1L).valeur(15.0).coefficient(2.0).type("EXAMEN").inscription(inscription).build();

        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(inscriptionRepository.findByEtudiantId(1L)).thenReturn(List.of(inscription));
        when(noteRepository.findByInscriptionId(1L)).thenReturn(List.of(note));

        byte[] result = service.exportBulletin(1L);

        assertNotNull(result);
        assertTrue(result.length > 0);
        // PDF signature: %PDF
        assertEquals('%', result[0]);
        assertEquals('P', result[1]);
        assertEquals('D', result[2]);
        assertEquals('F', result[3]);
    }

    @Test
    void exportListeEtudiants_whenCoursNotFound_shouldThrow() {
        when(coursRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.exportListeEtudiants(99L));
    }

    @Test
    void exportListeEtudiants_whenValid_shouldReturnPdf() {
        Cours cours = Cours.builder().id(1L).code("MATH101").nom("Mathématiques").build();
        Etudiant etudiant = Etudiant.builder().id(1L).matricule("MAT001").nom("Dupont").prenom("Jean").email("jean@test.com").build();
        Inscription inscription = Inscription.builder().id(1L).etudiant(etudiant).cours(cours).build();

        when(coursRepository.findById(1L)).thenReturn(Optional.of(cours));
        when(inscriptionRepository.findByCoursId(1L)).thenReturn(List.of(inscription));

        byte[] result = service.exportListeEtudiants(1L);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals('%', result[0]);
        assertEquals('P', result[1]);
    }

    @Test
    void exportRecuPaiement_whenPaiementNotFound_shouldThrow() {
        when(paiementRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.exportRecuPaiement(99L));
    }

    @Test
    void exportRecuPaiement_whenValid_shouldReturnPdf() {
        Etudiant etudiant = Etudiant.builder().nom("Dupont").prenom("Jean").build();
        Paiement paiement = Paiement.builder()
                .id(1L).reference("REF-001").montant(500.0).datePaiement(LocalDate.now())
                .modePaiement(ModePaiement.CARTE_BANCAIRE).statut(StatutPaiement.PAYE)
                .etudiant(etudiant).description("Frais inscription")
                .build();

        when(paiementRepository.findById(1L)).thenReturn(Optional.of(paiement));

        byte[] result = service.exportRecuPaiement(1L);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals('%', result[0]);
    }
}
