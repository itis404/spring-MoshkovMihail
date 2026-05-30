package com.example.tounip.tounip.auth.presentation.controller;

import com.example.tounip.tounip.auth.application.dto.AuthTokensDto;
import com.example.tounip.tounip.auth.application.dto.LoginCommand;
import com.example.tounip.tounip.auth.application.dto.RegisterCommand;
import com.example.tounip.tounip.auth.application.service.AuthService;
import com.example.tounip.tounip.auth.presentation.dto.AuthRequest;
import com.example.tounip.tounip.auth.presentation.dto.RefreshTokenRequest;
import com.example.tounip.tounip.auth.presentation.dto.RegistrationRequest;
import com.example.tounip.tounip.security.current.CurrentUserProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    private final AuthService authService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/register")
    public AuthTokensDto register(
            @Valid @RequestBody RegistrationRequest request,
            HttpServletRequest servletRequest
    ) {
        RegisterCommand command = RegisterCommand.builder()
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .preferredLanguage(request.getPreferredLanguage())
                .password(request.getPassword())
                .build();

        return authService.register(
                command,
                extractUserAgent(servletRequest),
                extractIpAddress(servletRequest)
        );
    }

    @PostMapping("/login")
    public AuthTokensDto login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest servletRequest
    ) {
        LoginCommand command = LoginCommand.builder()
                .phoneNumber(request.getPhoneNumber())
                .password(request.getPassword())
                .build();

        return authService.login(
                command,
                extractUserAgent(servletRequest),
                extractIpAddress(servletRequest)
        );
    }

    @PostMapping("/refresh")
    public AuthTokensDto refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
    }

    @PostMapping("/logout-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logoutAll() {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        authService.logoutAll(currentUserId);
    }

    private String extractUserAgent(HttpServletRequest request) {
        return request.getHeader(USER_AGENT_HEADER);
    }

    private String extractIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}