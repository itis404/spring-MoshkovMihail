package com.example.tounip.tounip.user.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserProfileDto {

    private UUID id;

    private String phoneNumber;

    private String email;

    private String username;

    private String publicName;

    private String preferredLanguage;

    private String role;

    private boolean active;
}