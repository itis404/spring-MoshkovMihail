package com.example.tounip.tounip.live.application.service;

import com.example.tounip.tounip.live.application.dto.LiveRoomJoinDto;

import java.util.UUID;

public interface LiveKitService {

    LiveRoomJoinDto joinChannelLiveRoom(UUID channelId, UUID currentUserId);
}