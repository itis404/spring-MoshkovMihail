package com.example.tounip.tounip.channel.presentation.controller;

import com.example.tounip.tounip.channel.application.dto.ChannelDto;
import com.example.tounip.tounip.channel.application.dto.CreateChannelCommand;
import com.example.tounip.tounip.channel.application.dto.UpdateChannelCommand;
import com.example.tounip.tounip.channel.application.service.ChannelService;
import com.example.tounip.tounip.channel.presentation.request.CreateChannelRequest;
import com.example.tounip.tounip.channel.presentation.request.UpdateChannelRequest;
import com.example.tounip.tounip.security.current.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelApiController {

    private final ChannelService channelService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/spaces/{spaceId}/channels")
    public List<ChannelDto> getChannelsBySpace(@PathVariable UUID spaceId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return channelService.findChannelsBySpace(spaceId, currentUserId);
    }

    @PostMapping("/spaces/{spaceId}/channels")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createChannel(
            @PathVariable UUID spaceId,
            @Valid @RequestBody CreateChannelRequest request
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        CreateChannelCommand command = CreateChannelCommand.builder()
                .spaceId(spaceId)
                .creatorId(currentUserId)
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return channelService.createChannel(command);
    }

    @GetMapping("/channels/{channelId}")
    public ChannelDto getChannelById(@PathVariable UUID channelId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return channelService.findById(channelId, currentUserId);
    }

    @PutMapping("/channels/{channelId}")
    public ChannelDto updateChannel(
            @PathVariable UUID channelId,
            @Valid @RequestBody UpdateChannelRequest request
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        UpdateChannelCommand command = UpdateChannelCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return channelService.updateChannel(channelId, command, currentUserId);
    }

    @DeleteMapping("/channels/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannel(@PathVariable UUID channelId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        channelService.deleteChannel(channelId, currentUserId);
    }
}