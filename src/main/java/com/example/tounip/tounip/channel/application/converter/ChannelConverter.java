package com.example.tounip.tounip.channel.application.converter;

import com.example.tounip.tounip.channel.application.dto.ChannelDto;
import com.example.tounip.tounip.channel.infrastructure.persistence.entity.ChannelEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ChannelConverter implements Converter<ChannelEntity, ChannelDto> {

    @Override
    public ChannelDto convert(ChannelEntity channel) {
        return ChannelDto.builder()
                .id(channel.getId())
                .spaceId(channel.getSpace().getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .build();
    }
}