package com.flekssina.chat.common.xml;

/**
 * Команда выхода из чата.
 */
public class XMLLogoutCommand implements XMLMessage {
    private static final long serialVersionUID = 1L;

    private final String sessionId;

    public XMLLogoutCommand(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toXML() {
        return "<command name=\"logout\">" +
                "<session>" + sessionId + "</session>" +
                "</command>";
    }

    @Override
    public XMLMessageType getType() {
        return XMLMessageType.LOGOUT;
    }
}