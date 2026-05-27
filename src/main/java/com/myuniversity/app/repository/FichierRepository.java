package com.myuniversity.app.repository;

import com.myuniversity.app.entity.Fichier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FichierRepository extends JpaRepository<Fichier, Long> {
    List<Fichier> findByEntiteIdAndEntiteType(Long entiteId, String entiteType);
    void deleteByEntiteIdAndEntiteType(Long entiteId, String entiteType);
}
