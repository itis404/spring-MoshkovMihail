package com.example.tounip.tounip.channel.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateChannelCommand {

    private String name;

    private String description;
}