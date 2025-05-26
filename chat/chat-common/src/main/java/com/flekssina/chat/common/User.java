package com.flekssina.chat.common;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String clientType;
    private String sessionId;

    public User(String username, String clientType) {
        this.username = username;
        this.clientType = clientType;
        this.sessionId = UUID.randomUUID().toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", username, clientType);
    }
}