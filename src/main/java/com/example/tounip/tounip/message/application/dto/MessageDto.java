package com.example.tounip.tounip.message.application.dto;

import com.example.tounip.tounip.message.infrastructure.persistence.entity.MessageEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MessageDto {

    private UUID id;

    private UUID channelId;

    private UUID authorId;

    private String authorUsername;

    private String authorPublicName;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    public static MessageDto toMessageDto(MessageEntity message) {
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