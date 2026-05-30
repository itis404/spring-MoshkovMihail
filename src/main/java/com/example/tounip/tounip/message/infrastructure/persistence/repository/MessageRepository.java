package com.example.tounip.tounip.message.infrastructure.persistence.repository;

import com.example.tounip.tounip.message.infrastructure.persistence.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    @Query("""
            select m
            from MessageEntity m
            join fetch m.author
            join fetch m.channel c
            where c.id = :channelId
            order by m.createdAt asc
            """)
    List<MessageEntity> findAllByChannelIdWithAuthor(
            @Param("channelId") UUID channelId
    );

    @Query("""
        select m
        from MessageEntity m
        join fetch m.channel c
        join fetch c.space
        where m.id = :id
        """)
    Optional<MessageEntity> findByIdWithChannelAndSpace(
            @Param("id") UUID id
    );
}