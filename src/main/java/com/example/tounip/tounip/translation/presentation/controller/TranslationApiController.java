package com.example.tounip.tounip.translation.presentation.controller;

import com.example.tounip.tounip.security.current.CurrentUserProvider;
import com.example.tounip.tounip.translation.application.dto.TranslationDto;
import com.example.tounip.tounip.translation.application.service.TranslationService;
import com.example.tounip.tounip.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/messages/{messageId}/translate")
@RequiredArgsConstructor
public class TranslationApiController {

    private final TranslationService translationService;
    private final CurrentUserProvider currentUserProvider;
    private final UserService userService;

    @PostMapping
    public TranslationDto translateMessage(
            @PathVariable UUID messageId,
            @RequestParam(required = false) String targetLang
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        String language = resolveTargetLanguage(currentUserId, targetLang);

        return translationService.translateMessage(
                messageId,
                currentUserId,
                language
        );
    }

    private String resolveTargetLanguage(UUID currentUserId, String targetLang) {
        if (targetLang != null && !targetLang.isBlank()) {
            return targetLang.trim().toLowerCase();
        }

        return userService.findPreferredLanguage(currentUserId);
    }
}