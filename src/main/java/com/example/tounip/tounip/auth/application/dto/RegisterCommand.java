package com.example.tounip.tounip.auth.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterCommand {

    private String phoneNumber;

    private String email;

    private String password;

    private String preferredLanguage;
}