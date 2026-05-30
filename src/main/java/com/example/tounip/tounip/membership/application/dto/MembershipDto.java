package com.example.tounip.tounip.membership.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MembershipDto {

    private UUID id;

    private UUID userId;

    private String username;

    private String publicName;

    private UUID spaceId;

    private String role;

    private LocalDateTime joinedAt;
}