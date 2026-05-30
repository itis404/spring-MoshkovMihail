package com.example.tounip.tounip.security.current;

import com.example.tounip.tounip.security.user.TounipUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityCurrentUserProvider implements CurrentUserProvider {

    @Override
    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof TounipUserDetails userDetails)) {
            throw new IllegalStateException("Unsupported principal type");
        }

        return userDetails.getId();
    }
}
