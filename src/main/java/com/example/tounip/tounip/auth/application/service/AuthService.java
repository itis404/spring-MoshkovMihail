package com.example.tounip.tounip.auth.application.service;

import com.example.tounip.tounip.auth.application.dto.AuthTokensDto;
import com.example.tounip.tounip.auth.application.dto.LoginCommand;
import com.example.tounip.tounip.auth.application.dto.RegisterCommand;

import java.util.UUID;

public interface AuthService {

    AuthTokensDto register(
            RegisterCommand command,
            String userAgent,
            String ipAddress
    );

    AuthTokensDto login(
            LoginCommand command,
            String userAgent,
            String ipAddress
    );

    AuthTokensDto refresh(String refreshToken);

    void logout(String refreshToken);

    void logoutAll(UUID currentUserId);
}