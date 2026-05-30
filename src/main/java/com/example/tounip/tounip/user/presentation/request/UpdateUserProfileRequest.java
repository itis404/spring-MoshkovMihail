package com.example.tounip.tounip.user.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username may contain only letters, digits and underscores"
    )
    private String username;

    @Size(min = 2, max = 50, message = "Public name must be between 2 and 50 characters")
    private String publicName;

    @Email(message = "Email should be valid")
    private String email;

    @Pattern(
            regexp = "^[a-z]{2}$",
            message = "Preferred language must be a two-letter language code"
    )
    private String preferredLanguage;
}