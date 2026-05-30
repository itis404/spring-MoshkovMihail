package com.example.tounip.tounip.security.jwt;

import com.example.tounip.tounip.user.application.dto.UserAuthInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    public String generateAccessToken(UserAuthInfo user) {
        return generateToken(user, accessTokenExpirationMs, ACCESS_TOKEN_TYPE);
    }

    public String generateRefreshToken(UserAuthInfo user) {
        return generateToken(user, refreshTokenExpirationMs, REFRESH_TOKEN_TYPE);
    }

    public String extractPhoneNumber(String token) {
        return extractClaims(token).getSubject();
    }

    public UUID extractUserId(String token) {
        String userId = extractClaims(token).get("userId", String.class);
        return UUID.fromString(userId);
    }

    public String extractTokenType(String token) {
        return extractClaims(token).get(TOKEN_TYPE_CLAIM, String.class);
    }

    public boolean isAccessTokenValid(String token) {
        return isTokenValid(token, ACCESS_TOKEN_TYPE);
    }

    public boolean isRefreshTokenValid(String token) {
        return isTokenValid(token, REFRESH_TOKEN_TYPE);
    }

    private boolean isTokenValid(String token, String expectedType) {
        try {
            Claims claims = extractClaims(token);

            String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
            Date expiration = claims.getExpiration();

            return expectedType.equals(tokenType)
                    && expiration != null
                    && expiration.after(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private String generateToken(UserAuthInfo user, long expirationMs, String tokenType) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(user.getPhoneNumber())
                .claim("userId", user.getId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}