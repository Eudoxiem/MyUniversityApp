package com.myuniversity.app.service;

import com.myuniversity.app.entity.Fichier;
import com.myuniversity.app.repository.FichierRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

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

    public Fichier upload(MultipartFile file, Long entiteId, String entiteType) throws IOException {
        String nomStockage = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path cheminFichier = Paths.get(uploadDir, nomStockage);

        Files.copy(file.getInputStream(), cheminFichier, StandardCopyOption.REPLACE_EXISTING);

        Fichier fichier = Fichier.builder()
                .nomOriginal(file.getOriginalFilename())
                .nomStockage(nomStockage)
                .chemin(cheminFichier.toString())
                .typeMime(file.getContentType())
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
    }

    public Path getCheminFichier(Long id) {
        Fichier fichier = getFichier(id);
        return Paths.get(fichier.getChemin());
    }
}
