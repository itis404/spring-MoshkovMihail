package com.example.tounip.tounip.membership.application.service;

import com.example.tounip.tounip.membership.application.dto.MembershipDto;

import java.util.List;
import java.util.UUID;

public interface MembershipService {

    void createOwnerMembership(UUID spaceId, UUID userId);

    boolean isMember(UUID spaceId, UUID userId);

    void requireMember(UUID spaceId, UUID userId);

    void requireOwner(UUID spaceId, UUID userId);

    void requireOwnerOrAdmin(UUID spaceId, UUID userId);

    MembershipDto joinPublicSpace(UUID spaceId, UUID userId);

    void leaveSpace(UUID spaceId, UUID userId);

    List<MembershipDto> findSpaceMembers(UUID spaceId, UUID currentUserId);
}