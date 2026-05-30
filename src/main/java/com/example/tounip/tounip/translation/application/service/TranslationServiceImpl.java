package com.example.tounip.tounip.translation.application.service;

import com.example.tounip.tounip.membership.application.service.MembershipService;
import com.example.tounip.tounip.message.application.dto.MessageForTranslationDto;
import com.example.tounip.tounip.message.application.service.MessageLookupService;
import com.example.tounip.tounip.message.infrastructure.persistence.entity.MessageEntity;
import com.example.tounip.tounip.translation.application.client.TranslationClient;
import com.example.tounip.tounip.translation.application.converter.MessageTranslationConverter;
import com.example.tounip.tounip.translation.application.dto.TranslationDto;
import com.example.tounip.tounip.translation.config.TranslationProperties;
import com.example.tounip.tounip.translation.infrastructure.persistence.entity.MessageTranslationEntity;
import com.example.tounip.tounip.translation.infrastructure.persistence.repository.MessageTranslationRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final MessageTranslationRepository messageTranslationRepository;

    private final MessageLookupService messageLookupService;
    private final MembershipService membershipService;

    private final EntityManager entityManager;

    private final MessageTranslationConverter messageTranslationConverter;

    private final TranslationClient translationClient;
    private final TranslationProperties translationProperties;

    @Override
    @Transactional
    public TranslationDto translateMessage(
            UUID messageId,
            UUID currentUserId,
            String targetLanguage
    ) {
        if (!translationProperties.enabled()) {
            throw new IllegalStateException("Translation is disabled");
        }

        String normalizedTargetLanguage = normalizeLanguage(targetLanguage);

        MessageForTranslationDto message = messageLookupService.findMessageForTranslation(messageId);

        membershipService.requireMember(message.getSpaceId(), currentUserId);

        return messageTranslationRepository.findByMessage_IdAndTargetLanguage(
                        message.getId(),
                        normalizedTargetLanguage
                )
                .map(messageTranslationConverter::convert)
                .orElseGet(() -> translateAndSave(message, normalizedTargetLanguage));
    }

    private TranslationDto translateAndSave(
            MessageForTranslationDto message,
            String targetLanguage
    ) {
        String sourceLanguage = translationProperties.sourceLanguage();

        String translatedText = translationClient.translate(
                message.getContent(),
                sourceLanguage,
                targetLanguage
        );

        MessageEntity messageReference = entityManager.getReference(
                MessageEntity.class,
                message.getId()
        );

        MessageTranslationEntity translation = MessageTranslationEntity.builder()
                .message(messageReference)
                .sourceLanguage(sourceLanguage)
                .targetLanguage(targetLanguage)
                .originalTextSnapshot(message.getContent())
                .translatedText(translatedText)
                .provider(translationProperties.provider())
                .build();

        MessageTranslationEntity savedTranslation = messageTranslationRepository.save(translation);

        return messageTranslationConverter.convert(savedTranslation);
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException("Target language is required");
        }

        return language.trim().toLowerCase();
    }
}