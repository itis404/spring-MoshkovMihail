package com.example.tounip.tounip.session.application.service;

public interface RefreshTokenService {

    String generateRefreshToken();

    String hashRefreshToken(String refreshToken);
}