package com.example.socialmediabackend.websocket;

import com.example.socialmediabackend.exception.ConversationNotFoundException;
import com.example.socialmediabackend.exception.UserNotParticipantException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class WebSocketExceptionHandler {
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleConversationNotFound(ConversationNotFoundException ex) {
        return ex.getMessage();
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleUserNotParticipant(UserNotParticipantException ex) {
        return ex.getMessage();
    }
}
