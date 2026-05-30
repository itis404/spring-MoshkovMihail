package com.example.tounip.tounip.membership.infrastructure.persistence.repository;

import com.example.tounip.tounip.membership.infrastructure.persistence.entity.MembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<MembershipEntity, UUID> {

    boolean existsByUser_IdAndSpace_Id(UUID userId, UUID spaceId);

    Optional<MembershipEntity> findByUser_IdAndSpace_Id(UUID userId, UUID spaceId);

    @Query("""
            select m
            from MembershipEntity m
            join fetch m.user
            where m.space.id = :spaceId
            order by m.joinedAt asc
            """)
    List<MembershipEntity> findAllBySpaceIdWithUser(@Param("spaceId") UUID spaceId);

    List<MembershipEntity> findAllByUser_IdOrderByJoinedAtDesc(UUID userId);
}
