package com.example.tounip.tounip.translation.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TranslationDto {

    private UUID id;

    private UUID messageId;

    private String sourceLanguage;

    private String targetLanguage;

    private String originalText;

    private String translatedText;

    private String provider;

    private LocalDateTime createdAt;
}