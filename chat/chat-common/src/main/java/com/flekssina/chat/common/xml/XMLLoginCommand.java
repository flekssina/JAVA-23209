package com.flekssina.chat.common.xml;

/**
 * Команда входа в чат.
 */
public class XMLLoginCommand implements XMLMessage {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String clientType;

    public XMLLoginCommand(String username, String clientType) {
        this.username = username;
        this.clientType = clientType;
    }

    public String getUsername() {
        return username;
    }

    public String getClientType() {
        return clientType;
    }

    @Override
    public String toXML() {
        return "<command name=\"login\">" +
                "<name>" + username + "</name>" +
                "<type>" + clientType + "</type>" +
                "</command>";
    }

    @Override
    public XMLMessageType getType() {
        return XMLMessageType.LOGIN;
    }
}