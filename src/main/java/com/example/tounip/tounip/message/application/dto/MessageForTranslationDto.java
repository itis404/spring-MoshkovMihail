package com.example.tounip.tounip.message.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MessageForTranslationDto {

    private UUID id;

    private UUID channelId;

    private UUID spaceId;

    private String content;
}