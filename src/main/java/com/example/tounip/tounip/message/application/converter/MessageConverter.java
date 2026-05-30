package com.example.tounip.tounip.message.application.converter;

import com.example.tounip.tounip.message.application.dto.MessageDto;
import com.example.tounip.tounip.message.infrastructure.persistence.entity.MessageEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MessageConverter implements Converter<MessageEntity, MessageDto> {

    @Override
    public MessageDto convert(MessageEntity message) {
        return MessageDto.builder()
                .id(message.getId())
                .channelId(message.getChannel().getId())
                .authorId(message.getAuthor().getId())
                .authorUsername(message.getAuthor().getUsername())
                .authorPublicName(message.getAuthor().getPublicName())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .editedAt(message.getEditedAt())
                .build();
    }
}