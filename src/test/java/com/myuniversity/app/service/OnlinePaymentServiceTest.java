package com.myuniversity.app.service;

import com.myuniversity.app.entity.*;
import com.myuniversity.app.repository.PaiementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnlinePaymentServiceTest {

    @Mock
    private PaiementRepository paiementRepository;

    private OnlinePaymentService service;

    @BeforeEach
    void setUp() {
        service = new OnlinePaymentService(paiementRepository);
    }

    @Test
    void initierPaiement_whenPaiementNotFound_shouldThrow() {
        when(paiementRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.initierPaiement(99L));
    }

    @Test
    void initierPaiement_whenAlreadyPaye_shouldThrow() {
        Paiement paiement = Paiement.builder().id(1L).montant(100.0).statut(StatutPaiement.PAYE).build();
        when(paiementRepository.findById(1L)).thenReturn(Optional.of(paiement));
        assertThrows(RuntimeException.class, () -> service.initierPaiement(1L));
    }

    @Test
    void initierPaiement_whenStripeFails_shouldThrow() {
        Paiement paiement = Paiement.builder().id(1L).montant(50.0).reference("REF-001").statut(StatutPaiement.EN_ATTENTE).build();
        when(paiementRepository.findById(1L)).thenReturn(Optional.of(paiement));
        assertThrows(RuntimeException.class, () -> service.initierPaiement(1L));
    }

    @Test
    void confirmerPaiement_whenPaiementNotFound_shouldThrow() {
        assertThrows(RuntimeException.class, () -> service.confirmerPaiement("pi_unknown", null));
    }
}
