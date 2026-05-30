package com.example.tounip.tounip.session.application.service;

import com.example.tounip.tounip.session.application.dto.RefreshSessionResult;
import com.example.tounip.tounip.user.application.dto.UserAuthInfo;

import java.util.UUID;

public interface UserSessionService {

    String createSession(
            UserAuthInfo user,
            String userAgent,
            String ipAddress
    );

    RefreshSessionResult refreshSession(String refreshToken);

    void revokeSession(String refreshToken);

    void revokeAllUserSessions(UUID userId);
}