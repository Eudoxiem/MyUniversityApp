package com.myuniversity.app.service;

import com.myuniversity.app.entity.Cours;
import com.myuniversity.app.repository.CoursRepository;
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
class CoursServiceTest {

    @Mock
    private CoursRepository repository;

    @InjectMocks
    private CoursService service;

    @Test
    void findByProfesseurId_shouldReturnList() {
        when(repository.findByProfesseurId(1L)).thenReturn(List.of(new Cours(), new Cours()));

        List<Cours> result = service.findByProfesseurId(1L);

        assertEquals(2, result.size());
    }

    @Test
    void findByCode_shouldReturnCours() {
        when(repository.findByCode("MATH101")).thenReturn(Optional.of(new Cours()));

        assertTrue(service.findByCode("MATH101").isPresent());
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete(1L);
        verify(repository).deleteById(1L);
    }
}
