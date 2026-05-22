package com.myuniversity.app.service;

import com.myuniversity.app.entity.Salle;
import com.myuniversity.app.repository.SalleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalleServiceTest {

    @Mock
    private SalleRepository repository;

    @InjectMocks
    private SalleService service;

    @Test
    void save_shouldReturnSavedSalle() {
        Salle salle = Salle.builder().code("A101").nom("Amphi 1").capacite(100).build();
        when(repository.save(salle)).thenReturn(salle);

        Salle result = service.save(salle);

        assertEquals("A101", result.getCode());
    }

    @Test
    void findByCode_shouldReturnSalle() {
        when(repository.findByCode("A101")).thenReturn(Optional.of(new Salle()));

        assertTrue(service.findByCode("A101").isPresent());
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void update_whenNotExists_shouldThrow() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.update(99L, new Salle()));
    }
}
