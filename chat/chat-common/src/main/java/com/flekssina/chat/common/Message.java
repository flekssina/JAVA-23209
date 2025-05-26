package com.flekssina.chat.common;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;
    private String sender;
    private String content;
    private LocalDateTime timestamp;
    private String sessionId;

    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    public Message(MessageType type, String sender, String content) {
        this();
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        switch (type) {
            case CHAT_MESSAGE:
                return String.format("[%s] %s: %s", timestamp.toString(), sender, content);
            case USER_JOINED:
                return String.format(">>> Пользователь %s присоединился к чату", sender);
            case USER_LEFT:
                return String.format("<<< Пользователь %s покинул чат", sender);
            default:
                return content;
        }
    }
}