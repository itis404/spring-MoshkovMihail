package com.example.tounip.tounip.space.infrastructure.persistence.repository;

import com.example.tounip.tounip.space.infrastructure.persistence.entity.SpaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpaceRepository extends JpaRepository<SpaceEntity, UUID> {

    @Query("""
            select s
            from SpaceEntity s
            join fetch s.owner
            where s.isPublic = true
            order by s.createdAt desc
            """)
    List<SpaceEntity> findAllPublicSpacesWithOwner();

    @Query("""
            select s
            from SpaceEntity s
            join fetch s.owner
            where s.id = :id
            """)
    Optional<SpaceEntity> findByIdWithOwner(@Param("id") UUID id);

    @Query("""
        select s
        from SpaceEntity s
        join fetch s.owner
        where s.isPublic = true
          and (
              select count(m)
              from MembershipEntity m
              where m.space = s
          ) >= :minMembers
        order by s.createdAt desc
        """)
    List<SpaceEntity> findPopularSpacesWithOwner(@Param("minMembers") long minMembers);

    @Query("""
        select distinct s
        from SpaceEntity s
        join fetch s.owner
        where exists (
            select m.id
            from MembershipEntity m
            where m.space = s
              and m.user.id = :userId
        )
        order by s.createdAt desc
        """)
    List<SpaceEntity> findAllSpacesByMemberUserIdWithOwner(
            @Param("userId") UUID userId
    );
}