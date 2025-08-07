package com.example.socialmediabackend.exception;

public class UserNotParticipantException extends RuntimeException {
    public UserNotParticipantException(String message) {
        super(message);
    }
}
