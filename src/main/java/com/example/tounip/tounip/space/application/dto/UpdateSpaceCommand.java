package com.example.tounip.tounip.space.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateSpaceCommand {

    private String name;

    private String description;

    private Boolean isPublic;
}
