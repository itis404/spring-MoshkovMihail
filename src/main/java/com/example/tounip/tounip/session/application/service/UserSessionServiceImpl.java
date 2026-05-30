package com.example.tounip.tounip.session.application.service;

import com.example.tounip.tounip.session.application.dto.RefreshSessionResult;
import com.example.tounip.tounip.session.infrastructure.persistence.entity.UserSessionEntity;
import com.example.tounip.tounip.session.infrastructure.persistence.repository.UserSessionRepository;
import com.example.tounip.tounip.user.application.converter.UserAuthInfoConverter;
import com.example.tounip.tounip.user.application.dto.UserAuthInfo;
import com.example.tounip.tounip.user.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;

    private final RefreshTokenService refreshTokenService;

    private final EntityManager entityManager;

    private final UserAuthInfoConverter userAuthInfoConverter;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public String createSession(
            UserAuthInfo user,
            String userAgent,
            String ipAddress
    ) {
        String refreshToken = refreshTokenService.generateRefreshToken();
        String refreshTokenHash = refreshTokenService.hashRefreshToken(refreshToken);

        UserEntity userReference = entityManager.getReference(
                UserEntity.class,
                user.getId()
        );

        UserSessionEntity session = UserSessionEntity.builder()
                .user(userReference)
                .refreshTokenHash(refreshTokenHash)
                .userAgent(normalizeUserAgent(userAgent))
                .ipAddress(ipAddress)
                .expiresAt(calculateExpirationTime())
                .build();

        userSessionRepository.save(session);

        return refreshToken;
    }

    @Override
    @Transactional
    public RefreshSessionResult refreshSession(String refreshToken) {
        String refreshTokenHash = refreshTokenService.hashRefreshToken(refreshToken);

        UserSessionEntity session = userSessionRepository.findByRefreshTokenHash(refreshTokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (!session.isActive()) {
            throw new IllegalArgumentException("Refresh session is not active");
        }

        UserEntity user = session.getUser();

        if (user.isDeleted() || user.isBanned()) {
            throw new IllegalArgumentException("Account is not active");
        }

        String newRefreshToken = refreshTokenService.generateRefreshToken();
        String newRefreshTokenHash = refreshTokenService.hashRefreshToken(newRefreshToken);

        session.rotateRefreshToken(
                newRefreshTokenHash,
                calculateExpirationTime()
        );

        return RefreshSessionResult.builder()
                .user(userAuthInfoConverter.convert(user))
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    @Transactional
    public void revokeSession(String refreshToken) {
        String refreshTokenHash = refreshTokenService.hashRefreshToken(refreshToken);

        UserSessionEntity session = userSessionRepository.findByRefreshTokenHash(refreshTokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        session.revoke();
    }

    @Override
    @Transactional
    public void revokeAllUserSessions(UUID userId) {
        userSessionRepository.findAllByUser_IdAndRevokedAtIsNull(userId)
                .forEach(UserSessionEntity::revoke);
    }

    private LocalDateTime calculateExpirationTime() {
        return LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpirationMs));
    }
    private String normalizeUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return null;
        }

        return userAgent.length() > 500
                ? userAgent.substring(0, 500)
                : userAgent;
    }
}
