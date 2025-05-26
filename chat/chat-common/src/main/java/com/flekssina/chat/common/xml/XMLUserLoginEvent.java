package com.flekssina.chat.common.xml;

/**
 * Событие входа пользователя.
 */
public class XMLUserLoginEvent implements XMLMessage {
    private static final long serialVersionUID = 1L;

    private final String username;

    public XMLUserLoginEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toXML() {
        return "<event name=\"userlogin\">" +
                "<name>" + username + "</name>" +
                "</event>";
    }

    @Override
    public XMLMessageType getType() {
        return XMLMessageType.USER_LOGIN;
    }
}