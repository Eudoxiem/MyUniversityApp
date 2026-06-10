package com.myuniversity.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "presences",
       uniqueConstraints = @UniqueConstraint(columnNames = {"etudiant_id", "cours_id", "date"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Cours cours;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Builder.Default
    private boolean present = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean justifie = false;

    private String justification;
}
