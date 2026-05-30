package com.example.tounip.tounip.channel.application.service;

import com.example.tounip.tounip.channel.application.converter.ChannelConverter;
import com.example.tounip.tounip.channel.application.dto.ChannelDto;
import com.example.tounip.tounip.channel.application.dto.CreateChannelCommand;
import com.example.tounip.tounip.channel.application.dto.UpdateChannelCommand;
import com.example.tounip.tounip.channel.infrastructure.persistence.entity.ChannelEntity;
import com.example.tounip.tounip.channel.infrastructure.persistence.repository.ChannelRepository;
import com.example.tounip.tounip.membership.application.service.MembershipService;
import com.example.tounip.tounip.space.application.service.SpaceLookupService;
import com.example.tounip.tounip.space.infrastructure.persistence.entity.SpaceEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;

    private final MembershipService membershipService;
    private final SpaceLookupService spaceLookupService;

    private final EntityManager entityManager;

    private final ChannelConverter channelConverter;

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findChannelsBySpace(UUID spaceId, UUID currentUserId) {
        spaceLookupService.requireExistingSpace(spaceId);
        membershipService.requireMember(spaceId, currentUserId);

        return channelRepository.findAllBySpace_IdOrderByCreatedAtAsc(spaceId)
                .stream()
                .map(channelConverter::convert)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto findById(UUID channelId, UUID currentUserId) {
        ChannelEntity channel = findChannelEntityByIdWithSpace(channelId);

        membershipService.requireMember(channel.getSpace().getId(), currentUserId);

        return channelConverter.convert(channel);
    }

    @Override
    @Transactional
    public ChannelDto createChannel(CreateChannelCommand command) {
        spaceLookupService.requireExistingSpace(command.getSpaceId());
        membershipService.requireOwnerOrAdmin(command.getSpaceId(), command.getCreatorId());

        SpaceEntity spaceReference = entityManager.getReference(
                SpaceEntity.class,
                command.getSpaceId()
        );

        ChannelEntity channel = ChannelEntity.builder()
                .space(spaceReference)
                .name(normalizeName(command.getName()))
                .description(normalizeDescription(command.getDescription()))
                .build();

        ChannelEntity savedChannel = channelRepository.save(channel);

        return channelConverter.convert(savedChannel);
    }

    @Override
    @Transactional
    public ChannelDto updateChannel(
            UUID channelId,
            UpdateChannelCommand command,
            UUID currentUserId
    ) {
        ChannelEntity channel = findChannelEntityByIdWithSpace(channelId);

        membershipService.requireOwnerOrAdmin(channel.getSpace().getId(), currentUserId);

        channel.setName(normalizeName(command.getName()));
        channel.setDescription(normalizeDescription(command.getDescription()));

        ChannelEntity savedChannel = channelRepository.save(channel);

        return channelConverter.convert(savedChannel);
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId, UUID currentUserId) {
        ChannelEntity channel = findChannelEntityByIdWithSpace(channelId);

        membershipService.requireOwnerOrAdmin(channel.getSpace().getId(), currentUserId);

        channelRepository.delete(channel);
    }

    private ChannelEntity findChannelEntityByIdWithSpace(UUID channelId) {
        return channelRepository.findByIdWithSpace(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Channel name is required");
        }

        return name.trim();
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}