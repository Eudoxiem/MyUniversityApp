package com.myuniversity.app.repository;

import com.myuniversity.app.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser_Id(Long userId);

    void deleteByDateExpirationBefore(Instant now);
}
