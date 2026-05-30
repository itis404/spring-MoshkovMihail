package com.example.tounip.tounip.translation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "translation")
public record TranslationProperties(
        boolean enabled,
        String baseUrl,
        String apiKey,
        String sourceLanguage,
        String provider
) {
}