package com.example.tounip.tounip.space.application.dto;

import com.example.tounip.tounip.space.infrastructure.persistence.entity.SpaceEntity;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SpaceDto implements Serializable {

    private UUID id;

    private String name;

    private String description;

    private Boolean isPublic;

    private UUID ownerId;

    private String ownerUsername;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static SpaceDto toSpaceDto(SpaceEntity space) {
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
