package com.myuniversity.app.repository;

import com.myuniversity.app.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByInscriptionId(Long inscriptionId);
}
