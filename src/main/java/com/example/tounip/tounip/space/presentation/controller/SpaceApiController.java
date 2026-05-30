package com.example.tounip.tounip.space.presentation.controller;

import com.example.tounip.tounip.security.current.CurrentUserProvider;
import com.example.tounip.tounip.space.application.dto.CreateSpaceCommand;
import com.example.tounip.tounip.space.application.dto.SpaceDto;
import com.example.tounip.tounip.space.application.dto.SpaceSearchCommand;
import com.example.tounip.tounip.space.application.dto.UpdateSpaceCommand;
import com.example.tounip.tounip.space.application.service.SpaceService;
import com.example.tounip.tounip.space.presentation.request.CreateSpaceRequest;
import com.example.tounip.tounip.space.presentation.request.UpdateSpaceRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
public class SpaceApiController {

    private final SpaceService spaceService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public List<SpaceDto> getPublicSpaces() {
        return spaceService.findAllPublicSpaces();
    }

    @GetMapping("/my")
    public List<SpaceDto> getMySpaces() {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return spaceService.findMySpaces(currentUserId);
    }

    @GetMapping("/search")
    public List<SpaceDto> searchSpaces(
            @RequestParam(required = false) String query
    ) {
        SpaceSearchCommand command = SpaceSearchCommand.builder()
                .query(query)
                .onlyPublic(true)
                .build();

        return spaceService.searchSpaces(command);
    }

    @GetMapping("/popular")
    public List<SpaceDto> getPopularSpaces(
            @RequestParam(defaultValue = "2") long minMembers
    ) {
        return spaceService.findPopularSpaces(minMembers);
    }

    @GetMapping("/{id}")
    public SpaceDto getSpaceById(@PathVariable UUID id) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        return spaceService.findByIdForUser(id, currentUserId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpaceDto createSpace(@Valid @RequestBody CreateSpaceRequest request) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        CreateSpaceCommand command = CreateSpaceCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isPublic(request.getIsPublic())
                .ownerId(currentUserId)
                .build();

        return spaceService.createSpace(command);
    }

    @PutMapping("/{id}")
    public SpaceDto updateSpace(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSpaceRequest request
    ) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        UpdateSpaceCommand command = UpdateSpaceCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isPublic(request.getIsPublic())
                .build();

        return spaceService.updateSpace(id, command, currentUserId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpace(@PathVariable UUID id) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        spaceService.deleteSpace(id, currentUserId);
    }
}