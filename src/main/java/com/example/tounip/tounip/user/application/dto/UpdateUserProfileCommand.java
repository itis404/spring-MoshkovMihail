package com.example.tounip.tounip.user.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserProfileCommand {

    private String username;

    private String publicName;

    private String email;

    private String preferredLanguage;
}