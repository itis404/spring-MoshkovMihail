package com.example.tounip.tounip.user.application.exception;

public class UsernameGenerationException extends RuntimeException {

    public UsernameGenerationException(String message) {
        super(message);
    }

    public UsernameGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
