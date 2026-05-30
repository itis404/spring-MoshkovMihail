package com.example.tounip.tounip.translation.application.service;

import com.example.tounip.tounip.translation.application.dto.TranslationDto;

import java.util.UUID;

public interface TranslationService {

    TranslationDto translateMessage(
            UUID messageId,
            UUID currentUserId,
            String targetLanguage
    );
}