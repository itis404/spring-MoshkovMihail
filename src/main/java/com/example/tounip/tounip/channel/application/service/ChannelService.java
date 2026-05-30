package com.example.tounip.tounip.channel.application.service;

import com.example.tounip.tounip.channel.application.dto.ChannelDto;
import com.example.tounip.tounip.channel.application.dto.CreateChannelCommand;
import com.example.tounip.tounip.channel.application.dto.UpdateChannelCommand;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    List<ChannelDto> findChannelsBySpace(UUID spaceId, UUID currentUserId);

    ChannelDto findById(UUID channelId, UUID currentUserId);

    ChannelDto createChannel(CreateChannelCommand command);

    ChannelDto updateChannel(UUID channelId, UpdateChannelCommand command, UUID currentUserId);

    void deleteChannel(UUID channelId, UUID currentUserId);
}