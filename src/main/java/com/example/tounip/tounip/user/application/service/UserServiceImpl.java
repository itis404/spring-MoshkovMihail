package com.example.tounip.tounip.user.application.service;

import com.example.tounip.tounip.user.application.converter.UserAuthInfoConverter;
import com.example.tounip.tounip.user.application.converter.UserProfileConverter;
import com.example.tounip.tounip.user.application.dto.CreateUserCommand;
import com.example.tounip.tounip.user.application.dto.UpdateUserProfileCommand;
import com.example.tounip.tounip.user.application.dto.UserAuthInfo;
import com.example.tounip.tounip.user.application.dto.UserProfileDto;
import com.example.tounip.tounip.user.application.exception.UsernameGenerationException;
import com.example.tounip.tounip.user.infrastructure.persistence.entity.UserEntity;
import com.example.tounip.tounip.user.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_LANGUAGE = "ru";

    private final UserRepository userRepository;

    private final UsernameGenerator usernameGenerator;

    private final UserProfileConverter userProfileConverter;
    private final UserAuthInfoConverter userAuthInfoConverter;

    @Override
    @Transactional
    public UserAuthInfo createUser(CreateUserCommand dto) {
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already registered");
        }

        String normalizedEmail = normalizeEmail(dto.getEmail());

        if (normalizedEmail != null && userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String username = usernameGenerator.generate();
        String preferredLanguage = normalizePreferredLanguageOrDefault(dto.getPreferredLanguage());

        UserEntity userEntity = UserEntity.builder()
                .phoneNumber(dto.getPhoneNumber())
                .email(normalizedEmail)
                .username(username)
                .preferredLanguage(preferredLanguage)
                .hashPassword(dto.getHashPassword())
                .build();

        try {
            UserEntity savedUser = userRepository.saveAndFlush(userEntity);
            return userAuthInfoConverter.convert(savedUser);
        } catch (DataIntegrityViolationException exception) {
            String message = exception.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_account_username")) {
                throw new UsernameGenerationException(
                        "Could not generate unique username",
                        exception
                );
            }

            throw exception;
        }
    }

    @Override
    public UserAuthInfo findByPhoneNumberForAuth(String phoneNumber) {
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("Invalid phone number or password"));
        return userAuthInfoConverter.convert(user);
    }

    @Override
    public UserProfileDto findProfileById(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userProfileConverter.convert(user);
    }

    @Override
    @Transactional
    public UserProfileDto updateProfile(UUID userId, UpdateUserProfileCommand command) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isDeleted() || user.isBanned()) {
            throw new IllegalArgumentException("User is not active");
        }

        String username = normalizeUsername(command.getUsername());
        String publicName = normalizePublicName(command.getPublicName());
        String email = normalizeEmail(command.getEmail());
        String preferredLanguage = normalizePreferredLanguage(command.getPreferredLanguage());

        if (username != null && !username.equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(username, userId)) {
                throw new IllegalArgumentException("Username already taken");
            }

            user.setUsername(username);
        }

        if (publicName != null) {
            user.setPublicName(publicName);
        }

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(email, userId)) {
                throw new IllegalArgumentException("Email already registered");
            }

            user.setEmail(email);
        }

        if (preferredLanguage != null) {
            user.setPreferredLanguage(preferredLanguage);
        }

        UserEntity savedUser = userRepository.save(user);
        return userProfileConverter.convert(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public String findPreferredLanguage(UUID userId) {
        return userRepository.findById(userId)
                .map(UserEntity::getPreferredLanguage)
                .filter(language -> language != null && !language.isBlank())
                .orElse(DEFAULT_LANGUAGE);
    }

    @Override
    @Transactional(readOnly = true)
    public void requireActiveUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isDeleted() || user.isBanned()) {
            throw new IllegalArgumentException("User is not active");
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        return username.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePublicName(String publicName) {
        if (publicName == null || publicName.isBlank()) {
            return null;
        }

        return publicName.trim();
    }

    private String normalizePreferredLanguage(String preferredLanguage) {
        if (preferredLanguage == null || preferredLanguage.isBlank()) {
            return null;
        }

        return preferredLanguage.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePreferredLanguageOrDefault(String preferredLanguage) {
        String normalizedLanguage = normalizePreferredLanguage(preferredLanguage);

        if (normalizedLanguage == null) {
            return DEFAULT_LANGUAGE;
        }

        return normalizedLanguage;
    }
}