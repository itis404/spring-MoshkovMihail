package com.example.tounip.tounip.space.application.converter;

import com.example.tounip.tounip.space.application.dto.SpaceDto;
import com.example.tounip.tounip.space.infrastructure.persistence.entity.SpaceEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SpaceConverter implements Converter<SpaceEntity, SpaceDto> {

    @Override
    public SpaceDto convert(SpaceEntity space) {
        return SpaceDto.builder()
                .id(space.getId())
                .name(space.getName())
                .description(space.getDescription())
                .isPublic(space.getIsPublic())
                .ownerId(space.getOwner().getId())
                .ownerUsername(space.getOwner().getUsername())
                .createdAt(space.getCreatedAt())
                .updatedAt(space.getUpdatedAt())
                .build();
    }
}