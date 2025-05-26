package com.flekssina.chat.common.xml;

/**
 * Команда запроса списка пользователей.
 */
public class XMLListCommand implements XMLMessage {
    private static final long serialVersionUID = 1L;

    private final String sessionId;

    public XMLListCommand(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toXML() {
        return "<command name=\"list\">" +
                "<session>" + sessionId + "</session>" +
                "</command>";
    }

    @Override
    public XMLMessageType getType() {
        return XMLMessageType.LIST;
    }
}