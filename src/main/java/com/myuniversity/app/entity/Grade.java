package com.myuniversity.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "grades")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double valeurFinale;

    private String mention;

    private LocalDate dateValidation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscription_id", nullable = false, unique = true)
    private Inscription inscription;
}
