package com.example.tounip.tounip.user.presentation.controller;

import com.example.tounip.tounip.security.current.CurrentUserProvider;
import com.example.tounip.tounip.user.application.dto.UpdateUserProfileCommand;
import com.example.tounip.tounip.user.application.dto.UserProfileDto;
import com.example.tounip.tounip.user.application.service.UserService;
import com.example.tounip.tounip.user.presentation.request.UpdateUserProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final CurrentUserProvider currentUserProvider;

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileDto getCurrentUser() {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return userService.findProfileById(currentUserId);
    }

    @PatchMapping("/me")
    public UserProfileDto updateCurrentUser(
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        UpdateUserProfileCommand command = UpdateUserProfileCommand.builder()
                .username(request.getUsername())
                .publicName(request.getPublicName())
                .email(request.getEmail())
                .preferredLanguage(request.getPreferredLanguage())
                .build();

        return userService.updateProfile(currentUserId, command);
    }
}