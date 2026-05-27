package com.myuniversity.app.service;

import com.myuniversity.app.entity.*;
import com.myuniversity.app.repository.EtudiantRepository;
import com.myuniversity.app.repository.PaiementRepository;
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
class PaiementServiceTest {

    @Mock
    private PaiementRepository paiementRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private PaiementService service;

    @Test
    void findAll_shouldReturnAllPaiements() {
        when(paiementRepository.findAll()).thenReturn(List.of(new Paiement()));

        List<Paiement> result = service.findAll();

        assertEquals(1, result.size());
        verify(paiementRepository).findAll();
    }

    @Test
    void findById_whenExists_shouldReturnPaiement() {
        Paiement paiement = Paiement.builder().id(1L).reference("PAY-001").build();
        when(paiementRepository.findById(1L)).thenReturn(Optional.of(paiement));

        Paiement result = service.findById(1L);

        assertNotNull(result);
        assertEquals("PAY-001", result.getReference());
    }

    @Test
    void findById_whenNotExists_shouldThrow() {
        when(paiementRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(99L));
    }

    @Test
    void save_shouldReturnSavedPaiement() {
        Etudiant etudiant = Etudiant.builder().id(1L).build();
        Paiement paiement = Paiement.builder()
                .reference("PAY-001").montant(500.0)
                .datePaiement(LocalDate.now())
                .modePaiement(ModePaiement.VIREMENT)
                .build();

        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(paiementRepository.save(paiement)).thenReturn(paiement);

        Paiement result = service.save(paiement, 1L);

        assertNotNull(result);
        assertEquals("PAY-001", result.getReference());
        assertEquals(etudiant, result.getEtudiant());
        verify(paiementRepository).save(paiement);
    }

    @Test
    void save_whenEtudiantNotFound_shouldThrow() {
        when(etudiantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.save(new Paiement(), 99L));
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete(1L);
        verify(paiementRepository).deleteById(1L);
    }

    @Test
    void update_whenExists_shouldReturnUpdatedPaiement() {
        Etudiant etudiant = Etudiant.builder().id(1L).build();
        Paiement existing = Paiement.builder().id(1L).reference("PAY-001").montant(500.0).etudiant(etudiant).build();
        Paiement updated = Paiement.builder().id(1L).reference("PAY-001").montant(600.0).build();

        when(paiementRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(paiementRepository.save(updated)).thenReturn(updated);

        Paiement result = service.update(1L, updated);

        assertEquals(600.0, result.getMontant());
    }

    @Test
    void update_whenNotExists_shouldThrow() {
        when(paiementRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.update(99L, new Paiement()));
    }
}
