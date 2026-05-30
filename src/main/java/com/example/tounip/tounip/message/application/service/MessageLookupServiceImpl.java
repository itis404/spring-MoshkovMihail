package com.example.tounip.tounip.message.application.service;

import com.example.tounip.tounip.message.application.dto.MessageForTranslationDto;
import com.example.tounip.tounip.message.infrastructure.persistence.entity.MessageEntity;
import com.example.tounip.tounip.message.infrastructure.persistence.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageLookupServiceImpl implements MessageLookupService {

    private final MessageRepository messageRepository;

    @Override
    @Transactional(readOnly = true)
    public MessageForTranslationDto findMessageForTranslation(UUID messageId) {
        MessageEntity message = messageRepository.findByIdWithChannelAndSpace(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        return MessageForTranslationDto.builder()
                .id(message.getId())
                .channelId(message.getChannel().getId())
                .spaceId(message.getChannel().getSpace().getId())
                .content(message.getContent())
                .build();
    }
}