package com.myuniversity.app.service;

import com.myuniversity.app.entity.Inscription;
import com.myuniversity.app.repository.InscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscriptionServiceTest {

    @Mock
    private InscriptionRepository repository;

    @InjectMocks
    private InscriptionService service;

    @Test
    void findByEtudiantId_shouldReturnList() {
        when(repository.findByEtudiantId(1L)).thenReturn(List.of(new Inscription()));

        List<Inscription> result = service.findByEtudiantId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void findByCoursId_shouldReturnList() {
        when(repository.findByCoursId(1L)).thenReturn(List.of(new Inscription(), new Inscription()));

        List<Inscription> result = service.findByCoursId(1L);

        assertEquals(2, result.size());
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertFalse(service.findById(99L).isPresent());
    }
}
