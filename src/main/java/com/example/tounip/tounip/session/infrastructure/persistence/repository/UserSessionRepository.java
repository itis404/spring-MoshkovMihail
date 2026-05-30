package com.example.tounip.tounip.session.infrastructure.persistence.repository;

import com.example.tounip.tounip.session.infrastructure.persistence.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSessionEntity, UUID> {

    Optional<UserSessionEntity> findByRefreshTokenHash(String refreshTokenHash);

    List<UserSessionEntity> findAllByUser_IdAndRevokedAtIsNull(UUID userId);
}