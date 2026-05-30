package com.example.tounip.tounip.user.application.service;

import com.example.tounip.tounip.user.application.exception.UsernameGenerationException;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class UuidUsernameGenerator implements UsernameGenerator {

    private static final String PREFIX = "user_";
    private static final int RANDOM_PART_LENGTH = 16;

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^user_[a-f0-9]{16}$");

    @Override
    public String generate() {
        String randomPart = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, RANDOM_PART_LENGTH);

        String username = PREFIX + randomPart;

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new UsernameGenerationException(
                    "Generated username has invalid format"
            );
        }

        return username;
    }
}