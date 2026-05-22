package com.myuniversity.app.repository;

import com.myuniversity.app.entity.Salle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalleRepository extends JpaRepository<Salle, Long> {
    Optional<Salle> findByCode(String code);
}
