package com.example.tounip.tounip.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthTokensDto {

    private String accessToken;

    private String refreshToken;
}