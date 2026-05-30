package com.example.tounip.tounip.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotBlank
    private String phoneNumber;

    @Email
    private String email;

    @Pattern(
            regexp = "^[a-z]{2}$",
            message = "Preferred language must be a two-letter language code"
    )
    private String preferredLanguage;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}