package com.example.tounip.tounip.translation.application.converter;

import com.example.tounip.tounip.translation.application.dto.TranslationDto;
import com.example.tounip.tounip.translation.infrastructure.persistence.entity.MessageTranslationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MessageTranslationConverter implements Converter<MessageTranslationEntity, TranslationDto> {

    @Override
    public TranslationDto convert(MessageTranslationEntity translation) {
        return TranslationDto.builder()
                .id(translation.getId())
                .messageId(translation.getMessage().getId())
                .sourceLanguage(translation.getSourceLanguage())
                .targetLanguage(translation.getTargetLanguage())
                .originalText(translation.getOriginalTextSnapshot())
                .translatedText(translation.getTranslatedText())
                .provider(translation.getProvider())
                .createdAt(translation.getCreatedAt())
                .build();
    }
}