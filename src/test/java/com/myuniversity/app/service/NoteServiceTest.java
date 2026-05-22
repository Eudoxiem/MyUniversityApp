package com.myuniversity.app.service;

import com.myuniversity.app.entity.Note;
import com.myuniversity.app.repository.NoteRepository;
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
class NoteServiceTest {

    @Mock
    private NoteRepository repository;

    @InjectMocks
    private NoteService service;

    @Test
    void findByInscriptionId_shouldReturnList() {
        when(repository.findByInscriptionId(1L)).thenReturn(List.of(new Note()));

        List<Note> result = service.findByInscriptionId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void save_shouldReturnSavedNote() {
        Note note = Note.builder().valeur(15.0).coefficient(1.0).build();
        when(repository.save(note)).thenReturn(note);

        Note result = service.save(note);

        assertEquals(15.0, result.getValeur());
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
