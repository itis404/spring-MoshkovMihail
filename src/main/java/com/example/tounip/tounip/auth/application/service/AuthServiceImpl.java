package com.example.tounip.tounip.auth.application.service;

import com.example.tounip.tounip.auth.application.dto.AuthTokensDto;
import com.example.tounip.tounip.auth.application.dto.LoginCommand;
import com.example.tounip.tounip.auth.application.dto.RegisterCommand;
import com.example.tounip.tounip.security.jwt.JwtService;
import com.example.tounip.tounip.session.application.dto.RefreshSessionResult;
import com.example.tounip.tounip.session.application.service.UserSessionService;
import com.example.tounip.tounip.user.application.dto.CreateUserCommand;
import com.example.tounip.tounip.user.application.dto.UserAuthInfo;
import com.example.tounip.tounip.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserSessionService userSessionService;

    @Override
    public AuthTokensDto register(
            RegisterCommand command,
            String userAgent,
            String ipAddress
    ) {
        String passwordHash = passwordEncoder.encode(command.getPassword());

        String preferredLanguage = normalizePreferredLanguage(command.getPreferredLanguage());

        CreateUserCommand createUserCommand = CreateUserCommand.builder()
                .phoneNumber(command.getPhoneNumber())
                .email(command.getEmail())
                .preferredLanguage(preferredLanguage)
                .hashPassword(passwordHash)
                .build();

        UserAuthInfo user = userService.createUser(createUserCommand);

        return createAuthTokens(user, userAgent, ipAddress);
    }

    @Override
    public AuthTokensDto login(
            LoginCommand command,
            String userAgent,
            String ipAddress
    ) {
        UserAuthInfo user = userService.findByPhoneNumberForAuth(command.getPhoneNumber());

        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is not active");
        }

        boolean passwordMatches = passwordEncoder.matches(
                command.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new IllegalArgumentException("Invalid phone number or password");
        }

        return createAuthTokens(user, userAgent, ipAddress);
    }

    @Override
    public AuthTokensDto refresh(String refreshToken) {
        RefreshSessionResult result = userSessionService.refreshSession(refreshToken);

        String accessToken = jwtService.generateAccessToken(result.getUser());

        return new AuthTokensDto(accessToken, result.getRefreshToken());
    }

    @Override
    public void logout(String refreshToken) {
        userSessionService.revokeSession(refreshToken);
    }

    @Override
    public void logoutAll(UUID currentUserId) {
        userSessionService.revokeAllUserSessions(currentUserId);
    }

    private AuthTokensDto createAuthTokens(
            UserAuthInfo user,
            String userAgent,
            String ipAddress
    ) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = userSessionService.createSession(user, userAgent, ipAddress);

        return new AuthTokensDto(accessToken, refreshToken);
    }

    private String normalizePreferredLanguage(String preferredLanguage) {
        if (preferredLanguage == null || preferredLanguage.isBlank()) {
            return "ru";
        }

        return preferredLanguage.trim().toLowerCase(Locale.ROOT);
    }
}