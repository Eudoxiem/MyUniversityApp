package com.myuniversity.app.repository;

import com.myuniversity.app.entity.TokenInvalide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface TokenInvalideRepository extends JpaRepository<TokenInvalide, Long> {

    Optional<TokenInvalide> findByJti(String jti);

    void deleteByDateExpirationBefore(Instant now);
}
