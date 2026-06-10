package com.myuniversity.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tokens_invalides")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TokenInvalide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jti;

    @Column(nullable = false)
    private Instant dateExpiration;

    @Column(nullable = false)
    private Instant dateInvalidation;
}
