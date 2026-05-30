package com.example.tounip.tounip.membership.application.converter;

import com.example.tounip.tounip.membership.application.dto.MembershipDto;
import com.example.tounip.tounip.membership.infrastructure.persistence.entity.MembershipEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MembershipConverter implements Converter<MembershipEntity, MembershipDto> {

    @Override
    public MembershipDto convert(MembershipEntity membership) {
        return MembershipDto.builder()
                .id(membership.getId())
                .userId(membership.getUser().getId())
                .username(membership.getUser().getUsername())
                .publicName(membership.getUser().getPublicName())
                .spaceId(membership.getSpace().getId())
                .role(membership.getRole().name())
                .joinedAt(membership.getJoinedAt())
                .build();
    }
}