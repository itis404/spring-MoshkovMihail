package com.example.tounip.tounip.translation.infrastructure.client;

import com.example.tounip.tounip.translation.application.client.TranslationClient;
import com.example.tounip.tounip.translation.config.TranslationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class LibreTranslateClient implements TranslationClient {

    private final RestClient.Builder restClientBuilder;
    private final TranslationProperties translationProperties;

    @Override
    public String translate(
            String text,
            String sourceLanguage,
            String targetLanguage
    ) {
        RestClient restClient = restClientBuilder
                .baseUrl(translationProperties.baseUrl())
                .build();

        LibreTranslateRequest request = LibreTranslateRequest.builder()
                .q(text)
                .source(sourceLanguage)
                .target(targetLanguage)
                .format("text")
                .apiKey(normalizeApiKey(translationProperties.apiKey()))
                .build();

        LibreTranslateResponse response = restClient.post()
                .uri("/translate")
                .body(request)
                .retrieve()
                .body(LibreTranslateResponse.class);

        if (response == null || response.getTranslatedText() == null) {
            throw new IllegalStateException("Translation API returned empty response");
        }

        return response.getTranslatedText();
    }

    private String normalizeApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }

        return apiKey;
    }
}