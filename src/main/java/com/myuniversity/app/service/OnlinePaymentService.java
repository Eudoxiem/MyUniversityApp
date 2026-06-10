package com.myuniversity.app.service;

import com.myuniversity.app.dto.paiement.PaymentIntentResponse;
import com.myuniversity.app.entity.ModePaiement;
import com.myuniversity.app.entity.Paiement;
import com.myuniversity.app.entity.StatutPaiement;
import com.myuniversity.app.repository.PaiementRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@Transactional
public class OnlinePaymentService {

    private final PaiementRepository paiementRepository;

    public OnlinePaymentService(PaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    public PaymentIntentResponse initierPaiement(Long paiementId) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

        if (paiement.getStatut() == StatutPaiement.PAYE) {
            throw new RuntimeException("Ce paiement a déjà été effectué");
        }

        if (paiement.getPaymentIntentId() != null) {
            try {
                PaymentIntent existing = PaymentIntent.retrieve(paiement.getPaymentIntentId());
                if ("requires_payment_method".equals(existing.getStatus())) {
                    log.info("PaymentIntent existant réutilisé - id: {}", existing.getId());
                    return buildResponse(paiement, existing.getId(), existing.getClientSecret());
                }
            } catch (StripeException e) {
                log.warn("Impossible de récupérer le PaymentIntent existant, création d'un nouveau");
            }
        }

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paiement.getMontant().longValue() * 100)
                    .setCurrency("eur")
                    .putMetadata("paiement_id", paiementId.toString())
                    .putMetadata("reference", paiement.getReference())
                    .setDescription("Paiement " + paiement.getReference())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            paiement.setPaymentIntentId(paymentIntent.getId());
            paiement.setModePaiement(ModePaiement.CARTE_EN_LIGNE);
            paiementRepository.save(paiement);

            log.info("PaymentIntent créé - id: {}, paiementId: {}, montant: {}",
                    paymentIntent.getId(), paiementId, paiement.getMontant());

            return buildResponse(paiement, paymentIntent.getId(), paymentIntent.getClientSecret());
        } catch (StripeException e) {
            log.error("Erreur Stripe lors de la création du PaymentIntent", e);
            throw new RuntimeException("Erreur lors de l'initiation du paiement : " + e.getMessage());
        }
    }

    public Paiement confirmerPaiement(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                String paiementIdStr = paymentIntent.getMetadata().get("paiement_id");
                if (paiementIdStr == null) {
                    throw new RuntimeException("Metadata paiement_id manquante");
                }

                Paiement paiement = paiementRepository.findById(Long.parseLong(paiementIdStr))
                        .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

                paiement.setStatut(StatutPaiement.PAYE);
                paiement.setDatePaiement(LocalDate.now());
                paiementRepository.save(paiement);

                log.info("Paiement confirmé - paiementId: {}, reference: {}",
                        paiement.getId(), paiement.getReference());

                return paiement;
            }

            throw new RuntimeException("Le paiement n'a pas abouti. Statut: " + paymentIntent.getStatus());
        } catch (StripeException e) {
            log.error("Erreur Stripe lors de la confirmation du paiement", e);
            throw new RuntimeException("Erreur lors de la confirmation du paiement : " + e.getMessage());
        }
    }

    private PaymentIntentResponse buildResponse(Paiement paiement, String paymentIntentId, String clientSecret) {
        return PaymentIntentResponse.builder()
                .paymentIntentId(paymentIntentId)
                .clientSecret(clientSecret)
                .paiementId(paiement.getId())
                .montant(paiement.getMontant().longValue())
                .statut(paiement.getStatut().name())
                .build();
    }
}
