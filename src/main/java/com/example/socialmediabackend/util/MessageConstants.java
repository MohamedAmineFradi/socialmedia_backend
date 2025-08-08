package com.example.socialmediabackend.util;

public final class MessageConstants {
    public static final int MAX_MESSAGE_LENGTH = 1000;
    public static final int MAX_MESSAGES_PER_MINUTE = 10;
    public static final String UNAUTHORIZED_SENDER_MESSAGE = "Invalid or mismatched sender ID. User can only send messages as themselves.";

    private MessageConstants() {
    }
}