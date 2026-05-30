package com.example.tounip.tounip.translation.application.client;

public interface TranslationClient {

    String translate(
            String text,
            String sourceLanguage,
            String targetLanguage
    );
}