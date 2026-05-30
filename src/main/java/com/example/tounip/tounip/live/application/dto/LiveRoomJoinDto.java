package com.example.tounip.tounip.live.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LiveRoomJoinDto {

    private String serverUrl;

    private String roomName;

    private String participantToken;
}