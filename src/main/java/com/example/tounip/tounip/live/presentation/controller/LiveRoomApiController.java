package com.example.tounip.tounip.live.presentation.controller;

import com.example.tounip.tounip.live.application.dto.LiveRoomJoinDto;
import com.example.tounip.tounip.live.application.service.LiveKitService;
import com.example.tounip.tounip.security.current.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/channels/{channelId}/live")
@RequiredArgsConstructor
public class LiveRoomApiController {

    private final LiveKitService liveKitService;

    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public LiveRoomJoinDto joinLiveRoom(@PathVariable UUID channelId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return liveKitService.joinChannelLiveRoom(channelId, currentUserId);
    }
}