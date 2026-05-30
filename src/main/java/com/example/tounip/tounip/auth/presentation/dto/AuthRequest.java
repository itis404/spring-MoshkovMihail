package com.example.tounip.tounip.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;
}