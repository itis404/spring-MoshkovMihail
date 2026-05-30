package com.example.tounip.tounip.space.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpaceSearchCommand {

    private String query;

    private Boolean onlyPublic;
}