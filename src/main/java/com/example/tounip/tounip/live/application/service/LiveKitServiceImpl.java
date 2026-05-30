package com.example.tounip.tounip.live.application.service;

import com.example.tounip.tounip.channel.application.service.ChannelLookupService;
import com.example.tounip.tounip.live.application.dto.LiveRoomJoinDto;
import com.example.tounip.tounip.live.config.LiveKitProperties;
import com.example.tounip.tounip.membership.application.service.MembershipService;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiveKitServiceImpl implements LiveKitService {

    private final LiveKitProperties liveKitProperties;

    private final ChannelLookupService channelLookupService;

    private final MembershipService membershipService;

    @Override
    public LiveRoomJoinDto joinChannelLiveRoom(UUID channelId, UUID currentUserId) {
        UUID spaceId = channelLookupService.findSpaceIdByChannelId(channelId);

        membershipService.requireMember(spaceId, currentUserId);

        String roomName = buildRoomName(channelId);
        String participantIdentity = currentUserId.toString();

        AccessToken token = new AccessToken(
                liveKitProperties.apiKey(),
                liveKitProperties.apiSecret()
        );

        token.setIdentity(participantIdentity);
        token.setName(participantIdentity);

        token.addGrants(
                new RoomJoin(true),
                new RoomName(roomName)
        );

        return LiveRoomJoinDto.builder()
                .serverUrl(liveKitProperties.url())
                .roomName(roomName)
                .participantToken(token.toJwt())
                .build();
    }

    private String buildRoomName(UUID channelId) {
        return "channel-" + channelId;
    }
}