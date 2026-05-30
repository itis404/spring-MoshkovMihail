package com.example.tounip.tounip.user.application.service;

import com.example.tounip.tounip.user.application.dto.CreateUserCommand;
import com.example.tounip.tounip.user.application.dto.UpdateUserProfileCommand;
import com.example.tounip.tounip.user.application.dto.UserAuthInfo;
import com.example.tounip.tounip.user.application.dto.UserProfileDto;

import java.util.UUID;

public interface UserService {

    UserAuthInfo createUser(CreateUserCommand dto);

    UserAuthInfo findByPhoneNumberForAuth(String phoneNumber);

    UserProfileDto findProfileById(UUID userId);

    UserProfileDto updateProfile(UUID userId, UpdateUserProfileCommand command);

    String findPreferredLanguage(UUID userId);

    void requireActiveUser(UUID userId);
}