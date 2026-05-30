package com.example.tounip.tounip.message.application.service;

import com.example.tounip.tounip.message.application.dto.CreateMessageCommand;
import com.example.tounip.tounip.message.application.dto.MessageDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    List<MessageDto> findMessagesByChannel(UUID channelId, UUID currentUserId);

    MessageDto createMessage(CreateMessageCommand command);
}