package com.myuniversity.app.service;

import com.myuniversity.app.entity.Fichier;
import com.myuniversity.app.repository.FichierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    @Mock
    private FichierRepository fichierRepository;

    private FileUploadService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        service = new FileUploadService(fichierRepository);
        ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());
    }

    @Test
    void upload_whenFileIsEmpty_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", new byte[0]);

        assertThrows(IOException.class, () -> service.upload(file, 1L, "COURS"));
    }

    @Test
    void upload_whenMimeTypeNotAllowed_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile("file", "test.exe", "application/x-msdownload", "data".getBytes());

        assertThrows(IOException.class, () -> service.upload(file, 1L, "COURS"));
    }

    @Test
    void upload_whenExtensionNotAllowed_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile("file", "test.exe", "text/plain", "data".getBytes());

        assertThrows(IOException.class, () -> service.upload(file, 1L, "COURS"));
    }

    @Test
    void upload_whenValid_shouldSaveAndReturn() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "document.pdf", "application/pdf", "pdf content".getBytes());

        when(fichierRepository.save(any(Fichier.class))).thenAnswer(i -> i.getArgument(0));

        Fichier result = service.upload(file, 1L, "COURS");

        assertNotNull(result);
        assertEquals("document.pdf", result.getNomOriginal());
        assertEquals("application/pdf", result.getTypeMime());
        assertEquals(1L, result.getEntiteId());
        assertEquals("COURS", result.getEntiteType());
        assertTrue(result.getNomStockage().contains("_document.pdf"));
        assertTrue(result.getChemin().contains(tempDir.toString()));
        verify(fichierRepository).save(any(Fichier.class));
    }

    @Test
    void getFichiersByEntite_shouldReturnList() {
        when(fichierRepository.findByEntiteIdAndEntiteType(1L, "COURS"))
                .thenReturn(List.of(new Fichier()));

        List<Fichier> result = service.getFichiersByEntite(1L, "COURS");

        assertEquals(1, result.size());
        verify(fichierRepository).findByEntiteIdAndEntiteType(1L, "COURS");
    }

    @Test
    void getFichier_whenExists_shouldReturn() {
        Fichier fichier = Fichier.builder().id(1L).nomOriginal("doc.pdf").build();
        when(fichierRepository.findById(1L)).thenReturn(Optional.of(fichier));

        Fichier result = service.getFichier(1L);

        assertEquals("doc.pdf", result.getNomOriginal());
    }

    @Test
    void getFichier_whenNotExists_shouldThrow() {
        when(fichierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getFichier(99L));
    }

    @Test
    void delete_shouldRemoveFromDiskAndRepository() throws IOException {
        Path filepath = tempDir.resolve("test_delete.pdf");
        java.nio.file.Files.write(filepath, "content".getBytes());
        Fichier fichier = Fichier.builder().id(1L).nomOriginal("test_delete.pdf").chemin(filepath.toString()).build();
        when(fichierRepository.findById(1L)).thenReturn(Optional.of(fichier));

        service.delete(1L);

        assertFalse(java.nio.file.Files.exists(filepath));
        verify(fichierRepository).delete(fichier);
    }

    @Test
    void getCheminFichier_shouldReturnPath() {
        Fichier fichier = Fichier.builder().id(1L).chemin(tempDir.resolve("doc.pdf").toString()).build();
        when(fichierRepository.findById(1L)).thenReturn(Optional.of(fichier));

        Path result = service.getCheminFichier(1L);

        assertEquals(tempDir.resolve("doc.pdf"), result);
    }
}
