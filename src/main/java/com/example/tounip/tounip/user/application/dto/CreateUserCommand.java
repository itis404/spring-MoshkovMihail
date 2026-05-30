package com.example.tounip.tounip.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserCommand {

    private String phoneNumber;

    private String email;

    private String preferredLanguage;

    private String hashPassword;
}
