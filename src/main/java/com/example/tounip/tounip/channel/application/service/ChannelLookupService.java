package com.example.tounip.tounip.channel.application.service;

import java.util.UUID;

public interface ChannelLookupService {

    UUID findSpaceIdByChannelId(UUID channelId);

    void requireExistingChannel(UUID channelId);
}