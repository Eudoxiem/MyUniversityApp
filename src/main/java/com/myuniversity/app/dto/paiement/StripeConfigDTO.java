package com.myuniversity.app.dto.paiement;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StripeConfigDTO {
    private String publishableKey;
}
