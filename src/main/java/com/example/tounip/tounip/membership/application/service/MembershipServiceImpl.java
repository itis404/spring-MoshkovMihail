package com.example.tounip.tounip.membership.application.service;

import com.example.tounip.tounip.common.exception.ForbiddenException;
import com.example.tounip.tounip.membership.application.converter.MembershipConverter;
import com.example.tounip.tounip.membership.application.dto.MembershipDto;
import com.example.tounip.tounip.membership.application.model.MembershipRole;
import com.example.tounip.tounip.membership.infrastructure.persistence.entity.MembershipEntity;
import com.example.tounip.tounip.membership.infrastructure.persistence.repository.MembershipRepository;
import com.example.tounip.tounip.space.application.service.SpaceLookupService;
import com.example.tounip.tounip.space.infrastructure.persistence.entity.SpaceEntity;
import com.example.tounip.tounip.user.application.service.UserService;
import com.example.tounip.tounip.user.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;

    private final SpaceLookupService spaceLookupService;
    private final UserService userService;

    private final EntityManager entityManager;

    private final MembershipConverter membershipConverter;

    @Override
    @Transactional
    public void createOwnerMembership(UUID spaceId, UUID userId) {
        userService.requireActiveUser(userId);
        spaceLookupService.requireExistingSpace(spaceId);

        if (membershipRepository.existsByUser_IdAndSpace_Id(userId, spaceId)) {
            return;
        }

        UserEntity userReference = entityManager.getReference(UserEntity.class, userId);
        SpaceEntity spaceReference = entityManager.getReference(SpaceEntity.class, spaceId);

        MembershipEntity membership = MembershipEntity.builder()
                .user(userReference)
                .space(spaceReference)
                .role(MembershipRole.OWNER)
                .build();

        membershipRepository.save(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMember(UUID spaceId, UUID userId) {
        return membershipRepository.existsByUser_IdAndSpace_Id(userId, spaceId);
    }

    @Override
    @Transactional(readOnly = true)
    public void requireMember(UUID spaceId, UUID userId) {
        requireRoleAtLeast(spaceId, userId, MembershipRole.MEMBER);
    }

    @Override
    @Transactional(readOnly = true)
    public void requireOwner(UUID spaceId, UUID userId) {
        requireRoleAtLeast(spaceId, userId, MembershipRole.OWNER);
    }

    @Override
    @Transactional(readOnly = true)
    public void requireOwnerOrAdmin(UUID spaceId, UUID userId) {
        requireRoleAtLeast(spaceId, userId, MembershipRole.ADMIN);
    }

    @Override
    @Transactional
    public MembershipDto joinPublicSpace(UUID spaceId, UUID userId) {
        userService.requireActiveUser(userId);
        spaceLookupService.requirePublicSpace(spaceId);

        return membershipRepository.findByUser_IdAndSpace_Id(userId, spaceId)
                .map(membershipConverter::convert)
                .orElseGet(() -> createMemberMembership(spaceId, userId));
    }

    @Override
    @Transactional
    public void leaveSpace(UUID spaceId, UUID userId) {
        MembershipEntity membership = getMembership(spaceId, userId);

        if (membership.getRole() == MembershipRole.OWNER) {
            throw new ForbiddenException("Owner cannot leave own space");
        }

        membershipRepository.delete(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipDto> findSpaceMembers(UUID spaceId, UUID currentUserId) {
        requireMember(spaceId, currentUserId);

        return membershipRepository.findAllBySpaceIdWithUser(spaceId)
                .stream()
                .map(membershipConverter::convert)
                .toList();
    }

    private MembershipDto createMemberMembership(UUID spaceId, UUID userId) {
        UserEntity userReference = entityManager.getReference(UserEntity.class, userId);
        SpaceEntity spaceReference = entityManager.getReference(SpaceEntity.class, spaceId);

        MembershipEntity membership = MembershipEntity.builder()
                .user(userReference)
                .space(spaceReference)
                .role(MembershipRole.MEMBER)
                .build();

        MembershipEntity savedMembership = membershipRepository.save(membership);

        return membershipConverter.convert(savedMembership);
    }

    private void requireRoleAtLeast(
            UUID spaceId,
            UUID userId,
            MembershipRole requiredRole
    ) {
        MembershipEntity membership = getMembership(spaceId, userId);

        if (!membership.getRole().atLeast(requiredRole)) {
            throw new ForbiddenException("Not enough permissions");
        }
    }

    private MembershipEntity getMembership(UUID spaceId, UUID userId) {
        return membershipRepository.findByUser_IdAndSpace_Id(userId, spaceId)
                .orElseThrow(() -> new ForbiddenException("User is not a member of this space"));
    }
}