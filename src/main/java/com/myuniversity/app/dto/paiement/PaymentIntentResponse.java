package com.myuniversity.app.dto.paiement;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PaymentIntentResponse {
    private String paymentIntentId;
    private String clientSecret;
    private Long paiementId;
    private Long montant;
    private String statut;
}
