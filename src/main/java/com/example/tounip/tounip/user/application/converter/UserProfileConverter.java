package com.example.tounip.tounip.user.application.converter;

import com.example.tounip.tounip.user.application.dto.UserProfileDto;
import com.example.tounip.tounip.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserProfileConverter implements Converter<UserEntity, UserProfileDto> {

    @Override
    public UserProfileDto convert(UserEntity user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .username(user.getUsername())
                .publicName(user.getPublicName())
                .preferredLanguage(user.getPreferredLanguage())
                .role(user.getRole().name())
                .active(!user.isDeleted() && !user.isBanned())
                .build();
    }
}