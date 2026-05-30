package com.example.tounip.tounip.channel.application.service;

import com.example.tounip.tounip.channel.infrastructure.persistence.entity.ChannelEntity;
import com.example.tounip.tounip.channel.infrastructure.persistence.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelLookupServiceImpl implements ChannelLookupService {

    private final ChannelRepository channelRepository;

    @Override
    @Transactional(readOnly = true)
    public UUID findSpaceIdByChannelId(UUID channelId) {
        ChannelEntity channel = channelRepository.findByIdWithSpace(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        return channel.getSpace().getId();
    }

    @Override
    @Transactional(readOnly = true)
    public void requireExistingChannel(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("Channel not found");
        }
    }
}
