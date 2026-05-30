package com.example.tounip.tounip.user.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "account",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_account_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_account_phone_number", columnNames = "phone_number"),
                @UniqueConstraint(name = "uk_account_email", columnNames = "email")
        }
)
public class UserEntity {

    private static final String DEFAULT_LANGUAGE = "ru";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone_number", nullable = false, length = 32)
    private String phoneNumber;

    @Column(length = 255)
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 32)
    private State state;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Role role;

    @Column(nullable = false, length = 32)
    private String username;

    @Column(name = "public_name", nullable = false, length = 50)
    private String publicName;

    @Builder.Default
    @Column(name = "preferred_language", length = 2, nullable = false)
    private String preferredLanguage = DEFAULT_LANGUAGE;

    @Column(name = "hash_password", nullable = false)
    private String hashPassword;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum State {
        ACTIVE, BANNED
    }

    public enum Role {
        USER, ADMIN
    }

    public boolean isBanned() {
        return this.state == State.BANNED;
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(this.isDeleted);
    }

    @PrePersist
    public void prePersist() {
        if (state == null) {
            state = State.ACTIVE;
        }

        if (role == null) {
            role = Role.USER;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (publicName == null || publicName.isBlank()) {
            publicName = username;
        }

        if (preferredLanguage == null || preferredLanguage.isBlank()) {
            preferredLanguage = DEFAULT_LANGUAGE;
        }

        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (publicName == null || publicName.isBlank()) {
            publicName = username;
        }

        if (preferredLanguage == null || preferredLanguage.isBlank()) {
            preferredLanguage = DEFAULT_LANGUAGE;
        }

        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}