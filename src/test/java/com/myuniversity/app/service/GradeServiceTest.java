package com.myuniversity.app.service;

import com.myuniversity.app.entity.Grade;
import com.myuniversity.app.repository.GradeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private GradeRepository repository;

    @InjectMocks
    private GradeService service;

    @Test
    void findByInscriptionId_shouldReturnGrade() {
        when(repository.findByInscriptionId(1L)).thenReturn(Optional.of(new Grade()));

        assertTrue(service.findByInscriptionId(1L).isPresent());
    }

    @Test
    void save_shouldReturnSavedGrade() {
        Grade grade = Grade.builder().valeurFinale(16.5).mention("Bien").build();
        when(repository.save(grade)).thenReturn(grade);

        Grade result = service.save(grade);

        assertEquals(16.5, result.getValeurFinale());
        assertEquals("Bien", result.getMention());
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
}
