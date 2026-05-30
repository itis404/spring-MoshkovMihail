package com.example.tounip.tounip.message.application.service;

import com.example.tounip.tounip.channel.application.service.ChannelLookupService;
import com.example.tounip.tounip.channel.infrastructure.persistence.entity.ChannelEntity;
import com.example.tounip.tounip.membership.application.service.MembershipService;
import com.example.tounip.tounip.message.application.converter.MessageConverter;
import com.example.tounip.tounip.message.application.dto.CreateMessageCommand;
import com.example.tounip.tounip.message.application.dto.MessageDto;
import com.example.tounip.tounip.message.infrastructure.persistence.entity.MessageEntity;
import com.example.tounip.tounip.message.infrastructure.persistence.repository.MessageRepository;
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
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final ChannelLookupService channelLookupService;
    private final MembershipService membershipService;
    private final UserService userService;

    private final EntityManager entityManager;

    private final MessageConverter messageConverter;

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> findMessagesByChannel(UUID channelId, UUID currentUserId) {
        UUID spaceId = channelLookupService.findSpaceIdByChannelId(channelId);

        membershipService.requireMember(spaceId, currentUserId);

        return messageRepository.findAllByChannelIdWithAuthor(channelId)
                .stream()
                .map(messageConverter::convert)
                .toList();
    }

    @Override
    @Transactional
    public MessageDto createMessage(CreateMessageCommand command) {
        userService.requireActiveUser(command.getAuthorId());

        UUID spaceId = channelLookupService.findSpaceIdByChannelId(command.getChannelId());

        membershipService.requireMember(spaceId, command.getAuthorId());

        ChannelEntity channelReference = entityManager.getReference(
                ChannelEntity.class,
                command.getChannelId()
        );

        UserEntity authorReference = entityManager.getReference(
                UserEntity.class,
                command.getAuthorId()
        );

        MessageEntity message = MessageEntity.builder()
                .channel(channelReference)
                .author(authorReference)
                .content(normalizeContent(command.getContent()))
                .build();

        MessageEntity savedMessage = messageRepository.save(message);

        return messageConverter.convert(savedMessage);
    }

    private String normalizeContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content is required");
        }

        return content.trim();
    }
}