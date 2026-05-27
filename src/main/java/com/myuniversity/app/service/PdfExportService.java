package com.myuniversity.app.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.myuniversity.app.entity.*;
import com.myuniversity.app.repository.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfExportService {

    private final EtudiantRepository etudiantRepository;
    private final NoteRepository noteRepository;
    private final InscriptionRepository inscriptionRepository;
    private final CoursRepository coursRepository;
    private final PaiementRepository paiementRepository;

    public PdfExportService(EtudiantRepository etudiantRepository,
                            NoteRepository noteRepository,
                            InscriptionRepository inscriptionRepository,
                            CoursRepository coursRepository,
                            PaiementRepository paiementRepository) {
        this.etudiantRepository = etudiantRepository;
        this.noteRepository = noteRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.coursRepository = coursRepository;
        this.paiementRepository = paiementRepository;
    }

    public byte[] exportBulletin(Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        List<Inscription> inscriptions = inscriptionRepository.findByEtudiantId(etudiantId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

        document.add(new Paragraph("BULLETIN DE NOTES", titleFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Étudiant : " + etudiant.getNom() + " " + etudiant.getPrenom(), headerFont));
        document.add(new Paragraph("Matricule : " + etudiant.getMatricule(), normalFont));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        for (String header : new String[]{"Cours", "Note", "Coefficient", "Type"}) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(cell);
        }

        for (Inscription inscription : inscriptions) {
            List<Note> notes = noteRepository.findByInscriptionId(inscription.getId());
            for (Note note : notes) {
                table.addCell(new Phrase(inscription.getCours().getNom(), normalFont));
                table.addCell(new Phrase(String.valueOf(note.getValeur()), normalFont));
                table.addCell(new Phrase(String.valueOf(note.getCoefficient()), normalFont));
                table.addCell(new Phrase(note.getType(), normalFont));
            }
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    public byte[] exportListeEtudiants(Long coursId) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        List<Inscription> inscriptions = inscriptionRepository.findByCoursId(coursId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

        document.add(new Paragraph("LISTE DES ÉTUDIANTS", titleFont));
        document.add(new Paragraph("Cours : " + cours.getCode() + " - " + cours.getNom(), headerFont));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        for (String header : new String[]{"Matricule", "Nom", "Prénom", "Email"}) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(cell);
        }

        for (Inscription inscription : inscriptions) {
            Etudiant e = inscription.getEtudiant();
            table.addCell(new Phrase(e.getMatricule(), normalFont));
            table.addCell(new Phrase(e.getNom(), normalFont));
            table.addCell(new Phrase(e.getPrenom(), normalFont));
            table.addCell(new Phrase(e.getEmail(), normalFont));
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    public byte[] exportRecuPaiement(Long paiementId) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A5);
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

        document.add(new Paragraph("REÇU DE PAIEMENT", titleFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Référence : " + paiement.getReference(), normalFont));
        document.add(new Paragraph("Étudiant : " + paiement.getEtudiant().getNom() + " " + paiement.getEtudiant().getPrenom(), normalFont));
        document.add(new Paragraph("Montant : " + paiement.getMontant() + " €", normalFont));
        document.add(new Paragraph("Date : " + paiement.getDatePaiement(), normalFont));
        document.add(new Paragraph("Mode : " + paiement.getModePaiement(), normalFont));
        document.add(new Paragraph("Statut : " + paiement.getStatut(), normalFont));
        if (paiement.getDescription() != null) {
            document.add(new Paragraph("Description : " + paiement.getDescription(), normalFont));
        }

        document.close();
        return out.toByteArray();
    }
}
