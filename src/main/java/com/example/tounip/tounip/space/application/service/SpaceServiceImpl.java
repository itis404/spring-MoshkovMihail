package com.example.tounip.tounip.space.application.service;

import com.example.tounip.tounip.membership.application.service.MembershipService;
import com.example.tounip.tounip.space.application.converter.SpaceConverter;
import com.example.tounip.tounip.space.application.dto.CreateSpaceCommand;
import com.example.tounip.tounip.space.application.dto.SpaceDto;
import com.example.tounip.tounip.space.application.dto.SpaceSearchCommand;
import com.example.tounip.tounip.space.application.dto.UpdateSpaceCommand;
import com.example.tounip.tounip.space.infrastructure.persistence.entity.SpaceEntity;
import com.example.tounip.tounip.space.infrastructure.persistence.repository.SpaceCriteriaRepository;
import com.example.tounip.tounip.space.infrastructure.persistence.repository.SpaceRepository;
import com.example.tounip.tounip.user.application.service.UserService;
import com.example.tounip.tounip.user.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceCriteriaRepository spaceCriteriaRepository;

    private final UserService userService;
    private final MembershipService membershipService;

    private final EntityManager entityManager;

    private final SpaceConverter spaceConverter;

    @Override
    @Cacheable(value = "public-spaces")
    @Transactional(readOnly = true)
    public List<SpaceDto> findAllPublicSpaces() {
        return spaceRepository.findAllPublicSpacesWithOwner()
                .stream()
                .map(spaceConverter::convert)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SpaceDto findById(UUID id) {
        SpaceEntity space = findSpaceEntityByIdWithOwner(id);
        return spaceConverter.convert(space);
    }

    @Override
    @Transactional(readOnly = true)
    public SpaceDto findByIdForUser(UUID id, UUID currentUserId) {
        SpaceEntity space = findSpaceEntityByIdWithOwner(id);

        if (!Boolean.TRUE.equals(space.getIsPublic())) {
            membershipService.requireMember(space.getId(), currentUserId);
        }

        return spaceConverter.convert(space);
    }

    @Override
    @CacheEvict(value = "public-spaces", allEntries = true)
    @Transactional
    public SpaceDto createSpace(CreateSpaceCommand command) {
        userService.requireActiveUser(command.getOwnerId());

        UserEntity ownerReference = entityManager.getReference(
                UserEntity.class,
                command.getOwnerId()
        );

        SpaceEntity space = SpaceEntity.builder()
                .name(normalizeName(command.getName()))
                .description(normalizeDescription(command.getDescription()))
                .isPublic(resolveIsPublic(command.getIsPublic()))
                .owner(ownerReference)
                .build();

        SpaceEntity savedSpace = spaceRepository.save(space);

        membershipService.createOwnerMembership(
                savedSpace.getId(),
                command.getOwnerId()
        );

        return SpaceDto.toSpaceDto(savedSpace);
    }

    @Override
    @CacheEvict(value = "public-spaces", allEntries = true)
    @Transactional
    public SpaceDto updateSpace(UUID id, UpdateSpaceCommand command, UUID currentUserId) {
        SpaceEntity space = findSpaceEntityByIdWithOwner(id);

        membershipService.requireOwner(space.getId(), currentUserId);

        space.setName(normalizeName(command.getName()));
        space.setDescription(normalizeDescription(command.getDescription()));
        space.setIsPublic(resolveIsPublic(command.getIsPublic()));

        SpaceEntity savedSpace = spaceRepository.save(space);

        return SpaceDto.toSpaceDto(savedSpace);
    }

    @Override
    @CacheEvict(value = "public-spaces", allEntries = true)
    @Transactional
    public void deleteSpace(UUID id, UUID currentUserId) {
        SpaceEntity space = findSpaceEntityByIdWithOwner(id);

        membershipService.requireOwner(space.getId(), currentUserId);

        spaceRepository.delete(space);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpaceDto> searchSpaces(SpaceSearchCommand command) {
        return spaceCriteriaRepository.searchSpaces(command)
                .stream()
                .map(spaceConverter::convert)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpaceDto> findPopularSpaces(long minMembers) {
        long safeMinMembers = Math.max(minMembers, 1);

        return spaceRepository.findPopularSpacesWithOwner(safeMinMembers)
                .stream()
                .map(spaceConverter::convert)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpaceDto> findMySpaces(UUID currentUserId) {
        return spaceRepository.findAllSpacesByMemberUserIdWithOwner(currentUserId)
                .stream()
                .map(spaceConverter::convert)
                .toList();
    }

    private SpaceEntity findSpaceEntityByIdWithOwner(UUID id) {
        return spaceRepository.findByIdWithOwner(id)
                .orElseThrow(() -> new IllegalArgumentException("Space not found"));
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Space name is required");
        }

        return name.trim();
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }

    private Boolean resolveIsPublic(Boolean isPublic) {
        return isPublic == null || isPublic;
    }
}