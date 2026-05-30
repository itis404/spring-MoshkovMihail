package com.example.tounip.tounip.message.application.service;

import com.example.tounip.tounip.message.application.dto.MessageForTranslationDto;

import java.util.UUID;

public interface MessageLookupService {

    MessageForTranslationDto findMessageForTranslation(UUID messageId);
}