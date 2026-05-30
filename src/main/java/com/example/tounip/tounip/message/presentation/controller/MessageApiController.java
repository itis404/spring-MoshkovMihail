package com.example.tounip.tounip.message.presentation.controller;

import com.example.tounip.tounip.message.application.dto.CreateMessageCommand;
import com.example.tounip.tounip.message.application.dto.MessageDto;
import com.example.tounip.tounip.message.application.service.MessageService;
import com.example.tounip.tounip.message.presentation.request.CreateMessageRequest;
import com.example.tounip.tounip.security.current.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels/{channelId}/messages")
@RequiredArgsConstructor
public class MessageApiController {

    private final MessageService messageService;

    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public List<MessageDto> getMessages(@PathVariable UUID channelId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return messageService.findMessagesByChannel(channelId, currentUserId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto createMessage(
            @PathVariable UUID channelId,
            @Valid @RequestBody CreateMessageRequest request
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        CreateMessageCommand command = CreateMessageCommand.builder()
                .channelId(channelId)
                .authorId(currentUserId)
                .content(request.getContent())
                .build();

        return messageService.createMessage(command);
    }
}