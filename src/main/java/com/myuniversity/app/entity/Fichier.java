package com.myuniversity.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fichiers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Fichier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomOriginal;

    @Column(nullable = false)
    private String nomStockage;

    @Column(nullable = false)
    private String chemin;

    @Column(nullable = false)
    private String typeMime;

    private Long taille;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateUpload = LocalDateTime.now();

    private Long entiteId;

    private String entiteType;
}
