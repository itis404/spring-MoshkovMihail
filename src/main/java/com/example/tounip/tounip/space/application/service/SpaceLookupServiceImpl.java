package com.example.tounip.tounip.space.application.service;

import com.example.tounip.tounip.common.exception.ForbiddenException;
import com.example.tounip.tounip.space.infrastructure.persistence.entity.SpaceEntity;
import com.example.tounip.tounip.space.infrastructure.persistence.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpaceLookupServiceImpl implements SpaceLookupService {

    private final SpaceRepository spaceRepository;

    @Override
    @Transactional(readOnly = true)
    public void requireExistingSpace(UUID spaceId) {
        if (!spaceRepository.existsById(spaceId)) {
            throw new IllegalArgumentException("Space not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void requirePublicSpace(UUID spaceId) {
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found"));

        if (!Boolean.TRUE.equals(space.getIsPublic())) {
            throw new ForbiddenException("Space is private");
        }
    }
}