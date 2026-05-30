package com.example.tounip.tounip.message.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateMessageCommand {

    private UUID channelId;

    private UUID authorId;

    private String content;
}