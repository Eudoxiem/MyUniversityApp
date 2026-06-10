package com.myuniversity.app.service;

import com.myuniversity.app.entity.Fichier;
import com.myuniversity.app.repository.FichierRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class FileUploadService {

    @Value("${application.upload.dir:uploads}")
    private String uploadDir;

    private final FichierRepository fichierRepository;

    public FileUploadService(FichierRepository fichierRepository) {
        this.fichierRepository = fichierRepository;
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(uploadDir));
    }

    private static final Set<String> MIMES_AUTORISES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain"
    );

    private static final Set<String> EXTENSIONS_AUTORISEES = Set.of(
            "jpg", "jpeg", "png", "gif", "webp",
            "pdf", "doc", "docx", "xls", "xlsx", "txt"
    );

    public Fichier upload(MultipartFile file, Long entiteId, String entiteType) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Le fichier est vide");
        }

        String contentType = file.getContentType();
        if (contentType == null || !MIMES_AUTORISES.contains(contentType.toLowerCase())) {
            throw new IOException("Type MIME non autorisé : " + contentType);
        }

        String originalFilename = sanitizeFilename(file.getOriginalFilename());
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IOException("Nom de fichier invalide");
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex + 1).toLowerCase();
        }
        if (!EXTENSIONS_AUTORISEES.contains(extension)) {
            throw new IOException("Extension de fichier non autorisée : " + extension);
        }

        String nomStockage = UUID.randomUUID() + "_" + originalFilename;
        Path cheminFichier = Paths.get(uploadDir, nomStockage).normalize();

        if (!cheminFichier.startsWith(Paths.get(uploadDir).normalize())) {
            throw new IOException("Tentative de path traversal détectée");
        }

        Files.copy(file.getInputStream(), cheminFichier, StandardCopyOption.REPLACE_EXISTING);

        log.info("Fichier uploadé - nom: {}, type: {}, taille: {}, entité: {}/{}",
                originalFilename, contentType, file.getSize(), entiteType, entiteId);

        Fichier fichier = Fichier.builder()
                .nomOriginal(originalFilename)
                .nomStockage(nomStockage)
                .chemin(cheminFichier.toString())
                .typeMime(contentType)
                .taille(file.getSize())
                .entiteId(entiteId)
                .entiteType(entiteType)
                .build();

        return fichierRepository.save(fichier);
    }

    public List<Fichier> getFichiersByEntite(Long entiteId, String entiteType) {
        return fichierRepository.findByEntiteIdAndEntiteType(entiteId, entiteType);
    }

    public Fichier getFichier(Long id) {
        return fichierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé avec l'id : " + id));
    }

    public void delete(Long id) throws IOException {
        Fichier fichier = getFichier(id);
        Files.deleteIfExists(Paths.get(fichier.getChemin()));
        fichierRepository.delete(fichier);
        log.info("Fichier supprimé - id: {}, nom: {}", id, fichier.getNomOriginal());
    }

    public Path getCheminFichier(Long id) {
        Fichier fichier = getFichier(id);
        return Paths.get(fichier.getChemin());
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) return null;
        return filename.replaceAll("[/\\\\:*?\"<>|]", "_")
                .replaceAll("\\s+", "_")
                .replaceAll("[\r\n]", "")
                .trim();
    }
}
