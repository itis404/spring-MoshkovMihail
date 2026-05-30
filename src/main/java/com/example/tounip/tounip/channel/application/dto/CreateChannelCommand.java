package com.example.tounip.tounip.channel.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateChannelCommand {

    private UUID spaceId;

    private UUID creatorId;

    private String name;

    private String description;
}