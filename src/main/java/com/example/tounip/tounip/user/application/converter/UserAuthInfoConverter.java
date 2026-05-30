package com.example.tounip.tounip.user.application.converter;

import com.example.tounip.tounip.user.application.dto.UserAuthInfo;
import com.example.tounip.tounip.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserAuthInfoConverter implements Converter<UserEntity, UserAuthInfo> {

    @Override
    public UserAuthInfo convert(UserEntity user) {
        return UserAuthInfo.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .username(user.getUsername())
                .publicName(user.getPublicName())
                .preferredLanguage(user.getPreferredLanguage())
                .role(user.getRole().name())
                .passwordHash(user.getHashPassword())
                .active(!user.isDeleted() && !user.isBanned())
                .build();
    }
}