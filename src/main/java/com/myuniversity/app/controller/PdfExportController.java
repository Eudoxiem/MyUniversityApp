package com.myuniversity.app.controller;

import com.myuniversity.app.service.PdfExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/export")
public class PdfExportController {

    private final PdfExportService pdfExportService;

    public PdfExportController(PdfExportService pdfExportService) {
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("/bulletin/{etudiantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR', 'ETUDIANT')")
    public ResponseEntity<byte[]> exportBulletin(@PathVariable Long etudiantId) {
        try {
            byte[] pdf = pdfExportService.exportBulletin(etudiantId);
            log.info("Bulletin exporté - étudiantId: {}", etudiantId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bulletin_" + etudiantId + ".pdf")
                    .body(pdf);
        } catch (RuntimeException e) {
            log.warn("Échec export bulletin - étudiantId: {}", etudiantId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/liste-etudiants/{coursId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public ResponseEntity<byte[]> exportListeEtudiants(@PathVariable Long coursId) {
        try {
            byte[] pdf = pdfExportService.exportListeEtudiants(coursId);
            log.info("Liste étudiants exportée - coursId: {}", coursId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=liste_etudiants_cours_" + coursId + ".pdf")
                    .body(pdf);
        } catch (RuntimeException e) {
            log.warn("Échec export liste étudiants - coursId: {}", coursId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/recu-paiement/{paiementId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ETUDIANT')")
    public ResponseEntity<byte[]> exportRecuPaiement(@PathVariable Long paiementId) {
        try {
            byte[] pdf = pdfExportService.exportRecuPaiement(paiementId);
            log.info("Reçu paiement exporté - paiementId: {}", paiementId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recu_paiement_" + paiementId + ".pdf")
                    .body(pdf);
        } catch (RuntimeException e) {
            log.warn("Échec export reçu paiement - paiementId: {}", paiementId);
            return ResponseEntity.notFound().build();
        }
    }
}
