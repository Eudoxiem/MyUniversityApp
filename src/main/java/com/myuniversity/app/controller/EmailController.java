package com.myuniversity.app.controller;

import com.myuniversity.app.dto.EmailRequestDTO;
import com.myuniversity.app.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequestDTO request) {
        try {
            emailService.envoyerEmail(request.getDestinataire(), request.getSujet(), request.getCorps());
            return ResponseEntity.ok("Email envoyé avec succès");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'envoi : " + e.getMessage());
        }
    }
}
