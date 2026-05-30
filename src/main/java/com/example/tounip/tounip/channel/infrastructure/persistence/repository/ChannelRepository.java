package com.example.tounip.tounip.channel.infrastructure.persistence.repository;

import com.example.tounip.tounip.channel.infrastructure.persistence.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<ChannelEntity, UUID> {

    List<ChannelEntity> findAllBySpace_IdOrderByCreatedAtAsc(UUID spaceId);

    @Query("""
            select c
            from ChannelEntity c
            join fetch c.space
            where c.id = :id
            """)
    Optional<ChannelEntity> findByIdWithSpace(@Param("id") UUID id);
}