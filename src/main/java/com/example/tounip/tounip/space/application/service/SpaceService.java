package com.example.tounip.tounip.space.application.service;

import com.example.tounip.tounip.space.application.dto.CreateSpaceCommand;
import com.example.tounip.tounip.space.application.dto.SpaceDto;
import com.example.tounip.tounip.space.application.dto.SpaceSearchCommand;
import com.example.tounip.tounip.space.application.dto.UpdateSpaceCommand;

import java.util.List;
import java.util.UUID;

public interface SpaceService {

    List<SpaceDto> findAllPublicSpaces();

    SpaceDto findById(UUID id);

    SpaceDto findByIdForUser(UUID id, UUID currentUserId);

    SpaceDto createSpace(CreateSpaceCommand command);

    SpaceDto updateSpace(UUID id, UpdateSpaceCommand command, UUID currentUserId);

    void deleteSpace(UUID id, UUID currentUserId);

    List<SpaceDto> searchSpaces(SpaceSearchCommand command);

    List<SpaceDto> findPopularSpaces(long minMembers);

    List<SpaceDto> findMySpaces(UUID currentUserId);
}