package com.example.tounip.tounip.translation.infrastructure.persistence.repository;

import com.example.tounip.tounip.translation.infrastructure.persistence.entity.MessageTranslationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MessageTranslationRepository extends JpaRepository<MessageTranslationEntity, UUID> {

    Optional<MessageTranslationEntity> findByMessage_IdAndTargetLanguage(
            UUID messageId,
            String targetLanguage
    );
}