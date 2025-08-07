package com.example.socialmediabackend.exception;

public class UnauthorizedSenderException extends RuntimeException {
    public UnauthorizedSenderException(String message) {
        super(message);
    }

    public UnauthorizedSenderException(String message, Throwable cause) {
        super(message, cause);
    }
}