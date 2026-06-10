package com.myuniversity.app.security;

import com.myuniversity.app.entity.User;
import com.myuniversity.app.repository.EtudiantRepository;
import com.myuniversity.app.repository.InscriptionRepository;
import com.myuniversity.app.repository.NoteRepository;
import com.myuniversity.app.repository.PaiementRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityHelper")
public class SecurityHelper {

    private final EtudiantRepository etudiantRepository;
    private final InscriptionRepository inscriptionRepository;
    private final NoteRepository noteRepository;
    private final PaiementRepository paiementRepository;

    public SecurityHelper(EtudiantRepository etudiantRepository,
                          InscriptionRepository inscriptionRepository,
                          NoteRepository noteRepository,
                          PaiementRepository paiementRepository) {
        this.etudiantRepository = etudiantRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.noteRepository = noteRepository;
        this.paiementRepository = paiementRepository;
    }

    public boolean estProprietaireEtudiant(Long etudiantId) {
        String email = getCurrentUserEmail();
        if (email == null) return false;
        return etudiantRepository.findById(etudiantId)
                .map(e -> e.getEmail().equals(email))
                .orElse(false);
    }

    public boolean estProprietaireInscription(Long inscriptionId) {
        String email = getCurrentUserEmail();
        if (email == null) return false;
        return inscriptionRepository.findById(inscriptionId)
                .map(i -> i.getEtudiant().getEmail().equals(email))
                .orElse(false);
    }

    public boolean estProprietaireNote(Long noteId) {
        String email = getCurrentUserEmail();
        if (email == null) return false;
        return noteRepository.findById(noteId)
                .map(n -> n.getInscription().getEtudiant().getEmail().equals(email))
                .orElse(false);
    }

    public boolean estProprietairePaiement(Long paiementId) {
        String email = getCurrentUserEmail();
        if (email == null) return false;
        return paiementRepository.findById(paiementId)
                .map(p -> p.getEtudiant().getEmail().equals(email))
                .orElse(false);
    }

    public boolean estProprietairePaiementByEtudiant(Long etudiantId) {
        String email = getCurrentUserEmail();
        if (email == null) return false;
        return etudiantRepository.findById(etudiantId)
                .map(e -> e.getEmail().equals(email))
                .orElse(false);
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getEmail();
        }
        return null;
    }
}
