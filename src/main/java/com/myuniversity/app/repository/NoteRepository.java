package com.myuniversity.app.repository;

import com.myuniversity.app.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByInscriptionId(Long inscriptionId);
}
