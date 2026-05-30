package com.example.tounip.tounip.membership.presentation.controller;

import com.example.tounip.tounip.membership.application.dto.MembershipDto;
import com.example.tounip.tounip.membership.application.service.MembershipService;
import com.example.tounip.tounip.security.current.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/spaces/{spaceId}")
@RequiredArgsConstructor
public class MembershipApiController {

    private final MembershipService membershipService;

    private final CurrentUserProvider currentUserProvider;

    @PostMapping("/join")
    public MembershipDto joinSpace(@PathVariable UUID spaceId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return membershipService.joinPublicSpace(spaceId, currentUserId);
    }

    @DeleteMapping("/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveSpace(@PathVariable UUID spaceId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        membershipService.leaveSpace(spaceId, currentUserId);
    }

    @GetMapping("/members")
    public List<MembershipDto> getSpaceMembers(@PathVariable UUID spaceId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return membershipService.findSpaceMembers(spaceId, currentUserId);
    }
}