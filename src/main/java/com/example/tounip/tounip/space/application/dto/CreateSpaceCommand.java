package com.example.tounip.tounip.space.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateSpaceCommand {

    private String name;

    private String description;

    private Boolean isPublic;

    private UUID ownerId;
}