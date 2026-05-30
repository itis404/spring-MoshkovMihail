package com.example.tounip.tounip.security.user;

import com.example.tounip.tounip.user.infrastructure.persistence.entity.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class TounipUserDetails implements UserDetails {

    private final UUID id;
    private final String phoneNumber;
    private final String passwordHash;
    private final String role;
    private final boolean active;

    public static TounipUserDetails fromEntity(UserEntity user) {
        return new TounipUserDetails(
                user.getId(),
                user.getPhoneNumber(),
                user.getHashPassword(),
                user.getRole().name(),
                !user.isDeleted() && !user.isBanned()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}