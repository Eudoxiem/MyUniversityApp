package com.myuniversity.app.controller;

import com.myuniversity.app.dto.FichierDTO;
import com.myuniversity.app.service.FileUploadService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/fichiers")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload/{entiteType}/{entiteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public ResponseEntity<FichierDTO> upload(
            @RequestParam("file") MultipartFile file,
            @PathVariable String entiteType,
            @PathVariable Long entiteId) {
        try {
            FichierDTO saved = FichierDTO.fromEntity(
                    fileUploadService.upload(file, entiteId, entiteType));
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public ResponseEntity<FichierDTO> getInfo(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(FichierDTO.fromEntity(fileUploadService.getFichier(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/entite/{entiteType}/{entiteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public List<FichierDTO> getByEntite(@PathVariable String entiteType, @PathVariable Long entiteId) {
        return fileUploadService.getFichiersByEntite(entiteId, entiteType).stream()
                .map(FichierDTO::fromEntity)
                .toList();
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws MalformedURLException {
        Path chemin = fileUploadService.getCheminFichier(id);
        Resource resource = new UrlResource(chemin.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = fileUploadService.getFichier(id).getTypeMime();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileUploadService.getFichier(id).getNomOriginal() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            fileUploadService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
